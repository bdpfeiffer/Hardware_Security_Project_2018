function carr = importFolderResp(folder)
if (~exist(folder, 'dir'))
    "couldnt find requested folder" %#ok
else
	lf = cd(folder);
    I = dir( "*.csv");
    % for the ith file in I read file to ith index
    for i = 1:1:length(I)
        vinfo = textscan(I(i).name,"resp_testPattern%d_%d.csv");
        carr{vinfo{1},vinfo{2}+1} = importVibRespFile(I(i).name);
    end
    cd(lf);
end