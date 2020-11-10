# BluetoothUtils
一款蓝牙SPP BLE 蓝牙 音箱操作 工具库

Step 1. Add the JitPack repository to your build file
        Add it in your root build.gradle at the end of repositories:

        allprojects {
          repositories {
            ...
            maven { url 'https://jitpack.io' }
          }
        }
        
Step 2. Add the dependency

      	dependencies {
	        implementation 'com.github.arpsyalin:BluetoothUtils:1.0'
	      }
	      
	      
命名还算较为规范一眼明了所以不列出接口了。
具体使用请参考app模块的demo

BLE的工具思想：
BluetoothLeManager是一个懒加载的单例。
BluetoothLeManager类负责了连接，断开，连接状态监听。

IScanBluetoothListener负责搜索回调监听
IConnectStateListener负责连接状态回调监听
BluetoothCallbackDeal类
负责数据接收分发处理
BluetoothLeManager初始化的时候会默认设置一个BluetoothCallbackDeal（蓝牙数据回调处理）的懒加载单例。

BluetoothCallbackDeal维护了一个IBluetoothDeal（蓝牙读取数据处理——数据处理接口）的Vector（一般这个数量个位数而已，因为考虑到多线程访问就用了Vector）

IBluetoothDeal接口负责实际的数据接收和发送处理。
声明一个类实现IBluetoothDeal这个接口添加进BluetoothCallbackDeal的Vector，连接成功一旦有符合你的实现你的实现类就可以收到。也可以通过writeData进行数据发送。


SPP的工具思想：
SppManage是一个懒加载的单例。
（采用Executors.newSingleThreadExecutor();使用线程池是因为1.执行线程都会取消关掉之前的线程，2.记得在华为手机好像有一个new thread的数量限制会有问题。）
包含了ConnectTask负责去连接,ConnectedTask负责连接成功后进行数据交互。
IScanBluetoothListener负责搜索回调监听
IConnectStateListener负责连接状态回调监听
DataCallBack负责SPP数据回调



