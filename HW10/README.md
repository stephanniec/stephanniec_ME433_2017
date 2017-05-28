### Low Pass Filter Observations

<b>Z Acceleration IMU Readings</b>
![plot](https://github.com/stephanniec/stephanniec_ME433_2017/blob/master/HW10/filter_results.png)

Observations:
* FIR and MAF both preserved the amplitude and overarching trend of the raw data
* MAF, however, did a better job of reducing noise and required less code (and therefore computation) than FIR
* While IIR did the best at reducing noise, too much filtering occurred
* This is shown by the green line above, which behaves in an overly damped manner

Conclusion: Of the 3 filters used to clean up the IMU signal, MAF performed the best.
