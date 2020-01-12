function [accel,gyro] = transformSample(vibrationResponses)
%UNTITLED2 Summary of this function goes here
%   Detailed explanation goes here
accelresp = zeros(length(vibrationResponses),4);
gyroresp  = zeros(length(vibrationResponses),4);
accelIndex = 1; gyroIndex = 1;
for i = 1:1:length(vibrationResponses)
    if ( vibrationResponses(i,2) ~= 0 )
        accelresp(accelIndex,:) = vibrationResponses(i,1:4);
        accelIndex = accelIndex + 1;
    else
        gyroresp(gyroIndex,:) = [vibrationResponses(i,1),vibrationResponses(i,5:7)];
        gyroIndex = gyroIndex + 1;
    end
end
accelresp(accelIndex:length(accelresp),:) = [];
gyroresp(gyroIndex:length(gyroresp),:) = [];
acceltime = accelresp(:,1);
accelvect = accelresp(:,2:4);
gyrotime = gyroresp(:,1);
gyrovect = gyroresp(:,2:4);

acceltr = linspace(min(acceltime), max(acceltime), length(acceltime));
accelvr = resample(accelvect, acceltr);

gyrotr = linspace(min(gyrotime), max(gyrotime), length(gyrotime));
gyrovr = resample(gyrovect, gyrotr);

L = length(acceltr);
Ts = mean(diff(acceltr));                 % Sampling Interval
Fs = 1/Ts;                                % Sampling Frequency
Fn = Fs/2;                                % Nyquist Frequency
accelFTvr = fft(accelvr)/L;               % Fourier Transform
accelFv = linspace(0, 1, fix(L/2)+1)*Fn;  % Frequency Vector
accelIv = 1:length(accelFv);              % Index Vector
accel = {accelFv, abs(accelFTvr(1:length(accelFv)))*2};
%plot(accelFv, abs(accelFTvr(1:length(accelFv)))*2)

L = length(gyrotr);
Ts = mean(diff(gyrotr));                 % Sampling Interval
Fs = 1/Ts;                                % Sampling Frequency
Fn = Fs/2;                                % Nyquist Frequency
gyroFTvr = fft(gyrovr)/L;               % Fourier Transform
gyroFv = linspace(0, 1, fix(L/2)+1)*Fn;  % Frequency Vector
gyroIv = 1:length(gyroFv);              % Index Vector
gyro = {gyroFv, abs(gyroFTvr(1:length(gyroFv)))*2};
end
