# Rastro
This is software for DIY PCB CNC laser machines.
Unlike 3D printer-based devices, this machine uses raster scanning to mitigate mechanical backlash and chassis flexing on acceleration.
A photoresist is exposed line-by-line akin to a laser printer.
It uses a linear optical encoder from a photo-printer to provide better positioning accuracy than just a stepper motor.
Each "pixel" is exposed with the same duration laser pulse.
Exposure takes place when the laser head moves with constant velocity to minimize acceleration-induced deviation.
The device can be used for both etching photoresist and solder mask applications.
