# Extended-Drive-Characterization
The FRC Characterization tool provides an easy way to get an insight into the physics behind robot motors.
It generates a robot project to be used in conjunction with the tool to extrapolate constants that describe the physical behavior of a selected motor.
While these constants accurately model the linear motion of the robot's drive base when both sides of the robot are running at similar speeds, it is an incomplete description, especially since one side may be slightly mechanically different from the other.
This repository aims to describe every motor on the drive base individually and to produce information needed to run a Kalman filter localization that can fuse data from encoders, accelerometer and gyroscope readings from a NavX IMU, and current readings from the TalonFX motor controller
