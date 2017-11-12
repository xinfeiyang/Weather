#Android的倒计时计数器

class MyCountTimer extends CountDownTimer {

    public MyCountTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    
    @Override
    public void onTick(long millisUntilFinished) {
        tv_send.setEnabled(false);
        tv_send.setText((millisUntilFinished / 1000) +"秒后重发");
    }
    
    @Override
    public void onFinish() {
        tv_send.setEnabled(true);
        tv_send.setText("重新发送验证码");
    }
    
}

MyCountTimer  timer = new MyCountTimer(60000,1000);

timer.start();






#创建定时任务;

AlarmManager manager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);

//每隔两个小时执行一次;

int time=2*60*60*1000;

long triggerAtTime= SystemClock.elapsedRealtime()+time;

Intent i=new Intent(this,AutoUpdateService.class);

PendingIntent pi=PendingIntent.getService(this,0,i,0);

manager.cancel(pi);

manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);

manager的set()方法需要传入3个参数，第一个参数是一个整形参数，用于指定AlarmManager的工作类型，有4种值可选，分别是ELAPSED_REALTIME、ELAPSED_REALTIME_WAKEUP、RTC和RTC_WAKEUP。其中ELAPSED_REALTIME表示让定时任务的触发时间从系统开机开始算起，但不会唤醒CPU。ELAPSED_REALTIME_WAKEUP同样表示让定时任务的触发时间从系统开机开始算起，但会唤醒CPU。RTC表示让定时任务的触发时间从1970年1月1日0点开始算起，但不会唤醒CPU。RTC_WAKEUP同样表示让定时任务的触发时间从1970年1月1日0点开始算起，但会唤醒CPU。使用SystemClock.elapsedRealTime（）方法可以获取到系统开机至今所经历时间的毫秒数，使用System.currentTimeMillis()方法可以获取到1970年1月1日0点至今所经历时间的毫秒数。

第二个参数就是定时任务触发的时间，以毫秒为单位。如果第一个参数使用的是ELAPSED_REALTIME、ELAPSED_REALTIME_WAKEUP，则这里传入开机至今的时间再加上延迟执行的时间。如果第一个参数使用的是RTC或RTC_WAKEUP，则这里传入1970年1月1日0点至今的时间再加上延迟执行的时间。

第三个参数是一个PendingIntent,这里一般会调用getService()方法或者getBroadcast()方法来获取一个能够执行服务或者广播的PendingIntent。这样当定时任务触发的时候，服务的onStartCommand（）方法或广播接收器的onReceive（）方法就可以得到执行。
