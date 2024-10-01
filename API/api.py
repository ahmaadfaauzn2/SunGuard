import os
import csv
import pickle
from datetime import datetime,timezone
import pytz
import statsmodels
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api, Resource, reqparse, fields, marshal_with, abort
import pandas as pd  # Import Pandas here

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///sensordata.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
api = Api(app)

def load_pickle_object(file_path):
    try:
        with open(file_path, 'rb') as file:
            return pickle.load(file)
    except Exception as e:
        print(f"Failed to load file {file_path}: {e}")
        return None

def load_and_prepare_data(file_path, delimiter=';'):
    try:
        data = pd.read_csv(file_path, delimiter=delimiter)
        data['Date'] = pd.to_datetime(data['Date'])
        data.set_index('Date', inplace=True)
        return data
    except Exception as e:
        print(f"Error loading data from {file_path}: {e}")
        return None


def load_and_prepare_data(file_path, delimiter=';'):
    """Load and prepare dataset with specified delimiter and date parsing."""
    try:
        data = pd.read_csv(file_path, delimiter=delimiter)
        data['Date'] = pd.to_datetime(data['Date'])
        data.set_index('Date', inplace=True)
        return data
    except FileNotFoundError:
        print(f"Data file not found at {file_path}. Please check the path.")
    except Exception as e:
        print(f"Error loading data: {e}")

def perform_prediction(model, data):
    """Perform prediction using an ARIMA model for the next one hour."""
    try:
        last_index = len(data) - 1  # Indeks terakhir dari data
        last_time = data.index[last_index]  # Waktu dari indeks terakhir

        # Prediksi satu jam ke depan
        predict_dif = model.predict(start=last_index, end=last_index)  # Memprediksi satu langkah ke depan
        last_known_value = data.iloc[-1]['uv_index']  # Nilai UV Index terakhir
        
        # Menghitung prediksi dari data asli
        predicted_value = last_known_value + predict_dif.iloc[0]  # Menambahkan perubahan yang diprediksi ke nilai terakhir
        predicted_time = last_time + pd.Timedelta(hours=1)  # Menambahkan satu jam ke waktu terakhir
        
        # Membuat Series dengan prediksi dan index waktu yang sesuai
        predictions = pd.Series([predicted_value], index=[predicted_time])
        return predictions
    except Exception as e:
        print(f"Error during prediction: {e}")


def datetime_to_index(input_datetime):
    # Fetch the data from the database ordered by timestamp
    data = SensorDataModel.query.order_by(SensorDataModel.timestamp).all()
    timestamps = [record.timestamp for record in data]

    # Find the closest index for the given datetime
    closest_index = min(range(len(timestamps)), key=lambda i: abs(timestamps[i] - input_datetime))
    return closest_index



# Load ARIMA model
with open('model/arima_cibiru.pkl', 'rb') as f:
    arima_model = pickle.load(f)


# Define the SensorData model
class SensorDataModel(db.Model):
    __tablename__ = 'sensordata'
    id = db.Column(db.Integer, primary_key=True)
    temperature = db.Column(db.String, nullable=False)
    altitude = db.Column(db.String, nullable=True)
    pressure = db.Column(db.String, nullable=True)
    uv_index = db.Column(db.String, nullable=True)
    timestamp = db.Column(db.DateTime, nullable=False, default=lambda: datetime.now(pytz.timezone('Asia/Jakarta')))


    def __repr__(self):
        return f"SensorData(temperature = {self.temperature}, altitude = {self.altitude}, pressure = {self.pressure}, uv_index = {self.uv_index}, timestamp = {self.timestamp})"
    
sensor_args = reqparse.RequestParser()
sensor_args.add_argument("temperature", type=str, help="Temperature is required", required=True)
sensor_args.add_argument("altitude", type=str, help="Altitude is required", required=False)
sensor_args.add_argument("pressure", type=str, help="Pressure is required", required=False)
sensor_args.add_argument("uv_index", type=str, help="UV Index is required", required=False)

sensor_fields = {
    'id': fields.Integer,
    'temperature': fields.String,
    'altitude': fields.String,
    'pressure': fields.String,
    'uv_index': fields.String,
    'timestamp': fields.DateTime
}

class ARIMAPrediction(Resource):
    def post(self):
        args = reqparse.RequestParser().parse_args()
        model_path = 'model/arima_model_4_final.pkl'
        data_path = 'data/datacibiru_final.csv'

        arima_model = load_pickle_object(model_path)
        if arima_model is None:
            return {"message": "Failed to load ARIMA model"}, 500

        new_data = load_and_prepare_data(data_path)
        if new_data is None or new_data.empty:  # Check if data is None or empty
            return {"message": "Failed to load data for prediction or data is empty"}, 500

        prediction_series = perform_prediction(arima_model, new_data)
        if prediction_series is None or prediction_series.empty:  # Check if prediction series is None or empty
            return {"message": "Prediction failed"}, 500

        prediction_results = [{
            'datetime': key.strftime("%Y-%m-%d %H:%M:%S"),
            'value': float(value)
        } for key, value in prediction_series.items()]

        return jsonify({
            'status': 'success',
            'predictions': prediction_results,
            'message': 'Prediction successful'
        })


        
class SensorData(Resource):
    @marshal_with(sensor_fields)
    def get(self):
        data = SensorDataModel.query.order_by(SensorDataModel.timestamp.desc()).limit(250).all()
        
        # data = SensorDataModel.query.all()

        return data
    
    @marshal_with(sensor_fields)
    def post(self):
        args = sensor_args.parse_args()
        sensor_data = SensorDataModel(
          temperature=args['temperature'],
          altitude=args.get('altitude', 'unknown'), 
          pressure=args.get('pressure', 'unknown'), 
          uv_index=args.get('uv_index', '0'),
          timestamp=datetime.now(pytz.timezone('Asia/Jakarta'))
          )
        db.session.add(sensor_data)
        db.session.commit()
     
    

     # Append data to CSV file
        csv_file_path = 'sensordata.csv'
        file_exists = os.path.isfile(csv_file_path)
        with open(csv_file_path, 'a', newline='') as file:
            fieldnames = ['id', 'temperature', 'altitude', 'pressure', 'uv_index', 'timestamp', 'timestamp']
            writer = csv.DictWriter(file, fieldnames=fieldnames)
            if not file_exists:
                writer.writeheader()  # File does not exist yet, write a header
            writer.writerow({
                'id': sensor_data.id,
                'temperature': sensor_data.temperature,
                'altitude': sensor_data.altitude,
                'pressure': sensor_data.pressure,
                'uv_index': sensor_data.uv_index,
                'timestamp': sensor_data.timestamp.strftime("%Y-%m-%d %H:%M:%S")  # Better formatting for CSV
                
            })

        return sensor_data, 201


class LatestSensorData(Resource):
    def get(self):
        latest_record = SensorDataModel.query.order_by(SensorDataModel.timestamp.desc()).first()
        if latest_record:
            return {
                'timestamp': latest_record.timestamp.strftime("%Y-%m-%d %H:%M:%S"),
                'uv_index': latest_record.uv_index
            }, 200
        else:
            return {
                'message': 'No data available'
            }, 404  
# class RetrainArima(Resource):
#     data = pd.read_csv("/kaggle/input/melbourne-uv/melbourne_data.csv")

# api.add_resource(SensorData, '/api/retrain')
api.add_resource(SensorData, '/api/sensordata')
api.add_resource(LatestSensorData, '/api/sensordata/latest')
api.add_resource(ARIMAPrediction, '/api/arima/predict')


if __name__ == '__main__':
    app.run(debug=True, port=5000)