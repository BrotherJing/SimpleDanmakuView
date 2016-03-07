[![star this repo](http://githubbadges.com/star.svg?user=Brotherjing&repo=SimpleDanmakuView&style=flat)](https://github.com/Brotherjing/SimpleDanmakuView)
[![fork this repo](http://githubbadges.com/fork.svg?user=Brotherjing&repo=SimpleDanmakuView&style=flat)](https://github.com/Brotherjing/SimpleDanmakuView/fork)

you can use this in your Android Studio project by adding
```
compile 'com.brotherjing:danmakuview:1.0.2'
```
to your module's ```build.gradle```

## Updates
in version 1.0.2, you can listen to click event on danmakuview. For example:
```
danmakuView.setOnDanmakuClickListener(new DanmakuView.OnDanmakuClickListener() {
    @Override
    public void onDanmakuClick(Danmaku danmaku) {
        Toast.makeText(MainActivity.this,danmaku.getText(),Toast.LENGTH_SHORT).show();
    }
});
```

------

in the 1.0.1 version, you can configure the DanmakuView by calling setMode(), and also change its size and even frame rate.
<img src="http://brotherjing.github.io/images/danmakuview.png"/>
