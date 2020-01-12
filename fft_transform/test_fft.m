accelresp = zeros(length(resptestPattern299),4);
j = 1;
for i = 1:1:length(resptestPattern299)
    if ( resptestPattern299(i,2) ~= 0)
        accelresp(j,:) = resptestPattern299(i,1:4);
    end
end 


Y = fft(X);

P2 = abs(Y/L);
P1 = P2(1:L/2+1);
P1(2:end-1) = 2*P1(2:end-1);