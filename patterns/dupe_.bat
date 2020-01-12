rem @echo OFF
setlocal enabledelayedexpansion
for %%F in (*_.csv) do (
	for /l %%i in (0, 1, 99) do (
		set num=0%%i
		set num=!num:~-2!
		copy "%%F" "%%~nF!num!%%~xF"
	)
)
