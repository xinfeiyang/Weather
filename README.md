展示天气信息

//创建定时任务;
AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
//每隔两个小时执行一次;
int time=2*60*60*1000;
long triggerAtTime= SystemClock.elapsedRealtime()+time;
Intent i=new Intent(this,AutoUpdateService.class);
PendingIntent pi=PendingIntent.getService(this,0,i,0);
manager.cancel(pi);
manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
