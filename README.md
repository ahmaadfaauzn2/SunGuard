# DESIGNING A WEARABLE DEVICE PROTOTYPE WITH ARTIFICIAL INTELLIGENCE FOR VEIL RECOMMENDATIONS SOLAR BASED ON UV INDEX

# Table of Contents
1. System Architecture
2. Block Diagram
3. Wiring Diagram
4. PCB Design
5. Specifications
6. Android Application
7. Hardware Components

# Architecture System
A high-level overview of the architecture of the system, illustrating the interactions between different components.
![architektur](https://github.com/user-attachments/assets/7f687096-7012-4b4b-a8d5-ee02f042fb18)

# System Architecture

## High-Level Overview:

```
[ESP32 with GUVA Sensor] --(BLE)--> [Android App] --(REST API)--> [AI Model] --> [SunGuard Device Sunscreen recommendation}
```


# Block Diagram
The block diagram represents the structure of the system, detailing how components and data flow through it.
![block diagramm](https://github.com/user-attachments/assets/31dd6d46-34dd-4082-a2ab-945a6ac66698)

# Wiring Diagram
The wiring diagram shows how the components are connected. This is useful for replicating the physical setup of the project.
![image](https://github.com/user-attachments/assets/f7485f31-e9d3-42c4-8840-3e29fa8811c0)

# PCB Design
This section provides the PCB layout used in the project. The PCB design makes the system more robust and scalable.
![image](https://github.com/user-attachments/assets/a8cf244f-c985-48e0-a3a9-a16696f34392)

# Specification 
Key specifications of the project, including details of the hardware.
![spec](https://github.com/user-attachments/assets/330900fc-8d01-496c-a3f6-2b62a11f813e)


# Android Apps 
This project includes an Android app for monitoring the system. Below are screenshots and descriptions of key features.
# Main Dashboard: Provides an overview of UV index data.
![android apps](https://github.com/user-attachments/assets/5a28f9b3-e746-4264-9840-d3de5228c53f)
# UV Index Data: Displays real-time UV index readings from the hardware.
![uvindexdata](https://github.com/user-attachments/assets/4235b360-11f3-495c-b3bd-36ddb80a44d3)
# UV Index Prediction: Shows UV index predictions based on historical data.
![uvindexpred](https://github.com/user-attachments/assets/036214d4-7874-4772-a2ea-e1f5ee245db8)

# Hardware Components
Here is a list of the hardware components used in this project:
- ESP32-WROOM-32: The microcontroller at the heart of the system.
- GUVA-S12SD Sensor: Used for UV light detection.
- PCB: Custom-designed PCB for easier integration.
- LEDs, Resistors, and other supporting components.
- Power Management : Lippo 3.7 V
![hardware](https://github.com/user-attachments/assets/3b77ae6d-d87b-4787-84af-8d64c50ea4b0)


# Installation and Setup Instructions
Provide step-by-step instructions on how to set up the hardware and software, including code installation, wiring details, and running the Android app.

# License
Include the licensing information for the project, if applicable.






