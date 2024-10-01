import json
import logging
import csv

# Setup logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# Function to process sensor data
def process_data(data):
    processed_data = []
    for entry in data:
        if None in entry.values():
            logging.warning(f"Incomplete data: {entry}")
        else:
            logging.info(f"Temperature: {entry['temperature']}, Altitude: {entry['altitude']}, "
                         f"Pressure: {entry['pressure']}, UV Index: {entry['uv_index']}")
            processed_data.append(entry)
    return processed_data

# Function to write processed data to CSV
def write_to_csv(processed_data, filename='processed_data.csv'):
    fieldnames = ['id', 'temperature', 'altitude', 'pressure', 'uv_index']
    with open(filename, mode='w', newline='') as csv_file:
        writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(processed_data)
    logging.info(f"Processed data written to {filename}")

# Sample data received from the GET method

data = [
     {
        "id": 1,
        "temperature": 10.4,
        "altitude": 910.0,
        "pressure": 25.0,
        "uv_index": 11.0
    },
    {
        "id": 2,
        "temperature": 10.4,
        "altitude": 910.0,
        "pressure": 25.0,
        "uv_index": 11.0
    },
    {
        "id": 3,
        "temperature": 10.4,
        "altitude": 910.0,
        "pressure": 25.0,
        "uv_index": 13.0
    },
    {
        "id": 4,
        "temperature": 10.4,
        "altitude": 910.0,
        "pressure": 25.0,
        "uv_index": 13.0
    },
    {
        "id": 5,
        "temperature": 10.4,
        "altitude": 910.0,
        "pressure": 25.0,
        "uv_index": 13.0
    },
    {
        "id": 6,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 7,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 8,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 9,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 10,
        "temperature": 29.94,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 11,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 12,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 13,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 14,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 15,
        "temperature": 29.96,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 16,
        "temperature": 29.98,
        "altitude": 0,
        "pressure": 0,
        "uv_index": 0
    },
    {
        "id": 17,
        "temperature": 29.538,
        "altitude": 760.348,
        "pressure": 922.228,
        "uv_index": 0
    },
    {
        "id": 18,
        "temperature": 29.553997,
        "altitude": 760.294,
        "pressure": 922.234,
        "uv_index": 0
    },
    {
        "id": 19,
        "temperature": 29.570002,
        "altitude": 760.276,
        "pressure": 922.236,
        "uv_index": 0
    },
    {
        "id": 20,
        "temperature": 29.570002,
        "altitude": 760.492,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 21,
        "temperature": 29.566,
        "altitude": 760.09607,
        "pressure": 922.25604,
        "uv_index": 0
    },
    {
        "id": 22,
        "temperature": 29.570002,
        "altitude": 760.276,
        "pressure": 922.236,
        "uv_index": 0
    },
    {
        "id": 23,
        "temperature": 29.572,
        "altitude": 760.204,
        "pressure": 922.24396,
        "uv_index": 0
    },
    {
        "id": 24,
        "temperature": 29.566,
        "altitude": 760.3121,
        "pressure": 922.23206,
        "uv_index": 0
    },
    {
        "id": 25,
        "temperature": 29.574,
        "altitude": 761.01404,
        "pressure": 922.154,
        "uv_index": 0
    },
    {
        "id": 26,
        "temperature": 29.592001,
        "altitude": 760.58203,
        "pressure": 922.20197,
        "uv_index": 0
    },
    {
        "id": 27,
        "temperature": 29.607998,
        "altitude": 760.52795,
        "pressure": 922.208,
        "uv_index": 0
    },
    {
        "id": 28,
        "temperature": 29.62,
        "altitude": 760.13196,
        "pressure": 922.25195,
        "uv_index": 0
    },
    {
        "id": 29,
        "temperature": 29.62,
        "altitude": 760.34796,
        "pressure": 922.228,
        "uv_index": 0
    },
    {
        "id": 30,
        "temperature": 29.623999,
        "altitude": 760.52795,
        "pressure": 922.208,
        "uv_index": 0
    },
    {
        "id": 31,
        "temperature": 29.635998,
        "altitude": 760.528,
        "pressure": 922.208,
        "uv_index": 0
    },
    {
        "id": 32,
        "temperature": 29.64,
        "altitude": 760.456,
        "pressure": 922.216,
        "uv_index": 0
    },
    {
        "id": 33,
        "temperature": 29.64,
        "altitude": 760.456,
        "pressure": 922.216,
        "uv_index": 0
    },
    {
        "id": 34,
        "temperature": 29.646,
        "altitude": 760.2399,
        "pressure": 922.24005,
        "uv_index": 0
    },
    {
        "id": 35,
        "temperature": 29.648,
        "altitude": 760.6,
        "pressure": 922.2,
        "uv_index": 0
    },
    {
        "id": 36,
        "temperature": 29.666,
        "altitude": 760.042,
        "pressure": 922.262,
        "uv_index": 0
    },
    {
        "id": 37,
        "temperature": 29.674,
        "altitude": 760.384,
        "pressure": 922.224,
        "uv_index": 0
    },
    {
        "id": 38,
        "temperature": 29.692001,
        "altitude": 760.474,
        "pressure": 922.214,
        "uv_index": 0
    },
    {
        "id": 39,
        "temperature": 29.692001,
        "altitude": 761.12195,
        "pressure": 922.14197,
        "uv_index": 0
    },
    {
        "id": 40,
        "temperature": 29.698002,
        "altitude": 760.474,
        "pressure": 922.21405,
        "uv_index": 0
    },
    {
        "id": 41,
        "temperature": 29.7,
        "altitude": 759.952,
        "pressure": 922.272,
        "uv_index": 0
    },
    {
        "id": 42,
        "temperature": 29.716,
        "altitude": 760.33,
        "pressure": 922.23,
        "uv_index": 0
    },
    {
        "id": 43,
        "temperature": 29.794,
        "altitude": 760.87,
        "pressure": 922.17004,
        "uv_index": 0
    },
    {
        "id": 44,
        "temperature": 29.801998,
        "altitude": 760.942,
        "pressure": 922.162,
        "uv_index": 0
    },
    {
        "id": 45,
        "temperature": 29.810001,
        "altitude": 760.6,
        "pressure": 922.2,
        "uv_index": 0
    },
    {
        "id": 46,
        "temperature": 29.812,
        "altitude": 760.51,
        "pressure": 922.20996,
        "uv_index": 0
    },
    {
        "id": 47,
        "temperature": 29.817999,
        "altitude": 760.744,
        "pressure": 922.18396,
        "uv_index": 0
    },
    {
        "id": 48,
        "temperature": 29.831997,
        "altitude": 760.492,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 49,
        "temperature": 29.855999,
        "altitude": 760.726,
        "pressure": 922.1859,
        "uv_index": 0
    },
    {
        "id": 50,
        "temperature": 29.911999,
        "altitude": 760.474,
        "pressure": 922.214,
        "uv_index": 0
    },
    {
        "id": 51,
        "temperature": 29.924,
        "altitude": 760.32996,
        "pressure": 922.23,
        "uv_index": 0
    },
    {
        "id": 52,
        "temperature": 29.936,
        "altitude": 760.54596,
        "pressure": 922.20593,
        "uv_index": 0
    },
    {
        "id": 53,
        "temperature": 29.939999,
        "altitude": 760.69,
        "pressure": 922.19006,
        "uv_index": 0
    },
    {
        "id": 54,
        "temperature": 29.948002,
        "altitude": 760.582,
        "pressure": 922.20197,
        "uv_index": 0
    },
    {
        "id": 55,
        "temperature": 29.959997,
        "altitude": 760.1859,
        "pressure": 922.246,
        "uv_index": 0
    },
    {
        "id": 56,
        "temperature": 29.959997,
        "altitude": 760.852,
        "pressure": 922.172,
        "uv_index": 0
    },
    {
        "id": 57,
        "temperature": 29.963999,
        "altitude": 760.56396,
        "pressure": 922.2039,
        "uv_index": 0
    },
    {
        "id": 58,
        "temperature": 29.963999,
        "altitude": 760.096,
        "pressure": 922.256,
        "uv_index": 0
    },
    {
        "id": 59,
        "temperature": 29.948002,
        "altitude": 760.582,
        "pressure": 922.20197,
        "uv_index": 0
    },
    {
        "id": 60,
        "temperature": 29.939999,
        "altitude": 760.69,
        "pressure": 922.19006,
        "uv_index": 0
    },
    {
        "id": 61,
        "temperature": 29.940002,
        "altitude": 760.492,
        "pressure": 922.2119,
        "uv_index": 0
    },
    {
        "id": 62,
        "temperature": 29.936,
        "altitude": 760.11395,
        "pressure": 922.254,
        "uv_index": 0
    },
    {
        "id": 63,
        "temperature": 29.939999,
        "altitude": 760.492,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 64,
        "temperature": 29.936,
        "altitude": 760.32996,
        "pressure": 922.23,
        "uv_index": 0
    },
    {
        "id": 65,
        "temperature": 29.944,
        "altitude": 760.186,
        "pressure": 922.246,
        "uv_index": 0
    },
    {
        "id": 66,
        "temperature": 29.956,
        "altitude": 760.474,
        "pressure": 922.214,
        "uv_index": 0
    },
    {
        "id": 67,
        "temperature": 29.971996,
        "altitude": 759.97003,
        "pressure": 922.27,
        "uv_index": 0
    },
    {
        "id": 68,
        "temperature": 29.967999,
        "altitude": 760.258,
        "pressure": 922.2381,
        "uv_index": 0
    },
    {
        "id": 69,
        "temperature": 29.98,
        "altitude": 760.492,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 70,
        "temperature": 29.984,
        "altitude": 760.132,
        "pressure": 922.2521,
        "uv_index": 0
    },
    {
        "id": 71,
        "temperature": 29.98,
        "altitude": 760.708,
        "pressure": 922.1881,
        "uv_index": 0
    },
    {
        "id": 72,
        "temperature": 29.98,
        "altitude": 760.492,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 73,
        "temperature": 29.976002,
        "altitude": 760.13196,
        "pressure": 922.25195,
        "uv_index": 0
    },
    {
        "id": 74,
        "temperature": 29.98,
        "altitude": 760.276,
        "pressure": 922.23596,
        "uv_index": 0
    },
    {
        "id": 75,
        "temperature": 29.98,
        "altitude": 760.49207,
        "pressure": 922.21204,
        "uv_index": 0
    },
    {
        "id": 76,
        "temperature": 29.982,
        "altitude": 760.204,
        "pressure": 922.24396,
        "uv_index": 0
    },
    {
        "id": 77,
        "temperature": 29.988,
        "altitude": 760.42004,
        "pressure": 922.22003,
        "uv_index": 0
    },
    {
        "id": 78,
        "temperature": 30.001999,
        "altitude": 760.38403,
        "pressure": 922.224,
        "uv_index": 0
    },
    {
        "id": 79,
        "temperature": 30.01,
        "altitude": 760.276,
        "pressure": 922.236,
        "uv_index": 0
    },
    {
        "id": 80,
        "temperature": 30.026001,
        "altitude": 760.438,
        "pressure": 922.21796,
        "uv_index": 0
    },
    {
        "id": 81,
        "temperature": 30.030003,
        "altitude": 760.15,
        "pressure": 922.25,
        "uv_index": 0
    },
    {
        "id": 82,
        "temperature": 30.034,
        "altitude": 760.438,
        "pressure": 922.21796,
        "uv_index": 0
    },
    {
        "id": 83,
        "temperature": 30.040003,
        "altitude": 760.438,
        "pressure": 922.21796,
        "uv_index": 0
    },
    {
        "id": 84,
        "temperature": 30.044,
        "altitude": 759.93396,
        "pressure": 922.2739,
        "uv_index": 0
    },
    {
        "id": 85,
        "temperature": 30.060001,
        "altitude": 759.8981,
        "pressure": 922.278,
        "uv_index": 0
    },
    {
        "id": 86,
        "temperature": 30.060001,
        "altitude": 760.132,
        "pressure": 922.2521,
        "uv_index": 0
    },
    {
        "id": 87,
        "temperature": 30.064,
        "altitude": 760.276,
        "pressure": 922.23596,
        "uv_index": 0
    },
    {
        "id": 88,
        "temperature": 30.079998,
        "altitude": 760.672,
        "pressure": 922.192,
        "uv_index": 0
    },
    {
        "id": 89,
        "temperature": 30.088001,
        "altitude": 760.312,
        "pressure": 922.23206,
        "uv_index": 0
    },
    {
        "id": 90,
        "temperature": 30.102001,
        "altitude": 760.24005,
        "pressure": 922.24005,
        "uv_index": 0
    },
    {
        "id": 91,
        "temperature": 30.108002,
        "altitude": 760.02405,
        "pressure": 922.26404,
        "uv_index": 0
    },
    {
        "id": 92,
        "temperature": 30.088001,
        "altitude": 759.62805,
        "pressure": 922.308,
        "uv_index": 0
    },
    {
        "id": 93,
        "temperature": 30.059998,
        "altitude": 760.79803,
        "pressure": 922.17804,
        "uv_index": 0
    },
    {
        "id": 94,
        "temperature": 30.034,
        "altitude": 760.438,
        "pressure": 922.21796,
        "uv_index": 0
    },
    {
        "id": 95,
        "temperature": 30.030003,
        "altitude": 760.366,
        "pressure": 922.22595,
        "uv_index": 0
    },
    {
        "id": 96,
        "temperature": 30.034002,
        "altitude": 760.22205,
        "pressure": 922.242,
        "uv_index": 0
    },
    {
        "id": 97,
        "temperature": 30.040003,
        "altitude": 760.222,
        "pressure": 922.242,
        "uv_index": 0
    },
    {
        "id": 98,
        "temperature": 30.060001,
        "altitude": 760.366,
        "pressure": 922.22595,
        "uv_index": 0
    },
    {
        "id": 99,
        "temperature": 30.056,
        "altitude": 759.97003,
        "pressure": 922.27,
        "uv_index": 0
    },
    {
        "id": 100,
        "temperature": 30.067999,
        "altitude": 760.654,
        "pressure": 922.19403,
        "uv_index": 0
    },
    {
        "id": 101,
        "temperature": 30.113335,
        "altitude": 760.195,
        "pressure": 922.24493,
        "uv_index": 0
    },
    {
        "id": 102,
        "temperature": 30.125998,
        "altitude": 760.83405,
        "pressure": 922.174,
        "uv_index": 0
    },
    {
        "id": 103,
        "temperature": 30.121998,
        "altitude": 759.97003,
        "pressure": 922.27,
        "uv_index": 0
    },
    {
        "id": 104,
        "temperature": 30.108002,
        "altitude": 760.24005,
        "pressure": 922.24005,
        "uv_index": 0
    },
    {
        "id": 105,
        "temperature": 30.095999,
        "altitude": 760.384,
        "pressure": 922.224,
        "uv_index": 0
    },
    {
        "id": 106,
        "temperature": 30.102001,
        "altitude": 760.45605,
        "pressure": 922.216,
        "uv_index": 0
    },
    {
        "id": 107,
        "temperature": 30.15,
        "altitude": 759.952,
        "pressure": 922.272,
        "uv_index": 0
    },
    {
        "id": 108,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 109,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 110,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 111,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 112,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 113,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    },
    {
        "id": 114,
        "temperature": 10.5,
        "altitude": 9150.0,
        "pressure": 255.0,
        "uv_index": 15.0
    }
    # Add more data entries here...

]

# Process the data
processed_data = process_data(data)

# Write processed data to CSV
write_to_csv(processed_data)

# Output the processed data for verification
print("Processed Data:")
for entry in processed_data:
    print(entry)
