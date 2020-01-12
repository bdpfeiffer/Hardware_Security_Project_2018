resp = zeros(length(testresp),4);
j = 1;
for i = 1:1:length(testresp)
   if ( testresp(i,2) ~= 0 )
       resp(j,:) = testresp(i,:);
       j = j + 1;
   end
end
resp(j:length(resp),:) = [];
time = resp(:,1);
vect = resp(:,2:4);

timestat = [mean(diff(time)) std(diff(time))]
tr = linspace(min(time), max(time), length(time));
vr = resample(vect, tr);

L = length(tr);
Ts = mean(diff(tr));                                        % Sampling Interval
Fs = 1/Ts;                                                  % Sampling Frequency
Fn = Fs/2;                                                  % Nyquist Frequency
FTvr = fft(vr)/L;                                           % Fourier Transform
Fv = linspace(0, 1, fix(L/2)+1)*Fn;                         % Frequency Vector
Iv = 1:length(Fv);                                          % Index Vector
figure(1)
plot(Fv, abs(FTvr(Iv))*2)
grid