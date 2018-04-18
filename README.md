### 关于
应用测试模式支持库，用来测试应用的各个指标项，如耗时、正确率和其他待测的相关信息，通过广播的形式把这些信息输出给其他应用收集。

### 特性
- 简单易用、高效稳定
- 插件化集成
- 与主应用代码分离，不侵入业务逻辑

### 功能列表
- 记录某个功能的耗时
- 记录当前业务执行结果参数
- 记录跟应用相关的关键信息
- 输出结果报表

### 用到的技术
- AOP
- 注解
- Gradle插件
- 抄的JakeWharton的Hugo项目


### 结构图
![](https://i.imgur.com/AYgXjOE.png)

### 接入  

- 在待测项目的根目录下的build.gradle里添加

		buildscript {
		    repositories {
		        jcenter()
		    }
	
		    dependencies {
		        classpath 'com.jiazy.testmode:plugin:0.1.35'
		    }
		}


- moudle里的gradle里添加

			apply plugin: 'com.jiazy.testmode'

- 注：现在@CollectSpentTimeSync、@CollectSpentTimeAsync、@CollectCountMsg、@CollectValueMsg都支持在同一个方法进行重复相同注解，因此需要在app模块下的build.gradle添加对Java8的支持：

        android {
            ...

            compileOptions {
                sourceCompatibility JavaVersion.VERSION_1_8
                targetCompatibility JavaVersion.VERSION_1_8
            }
        }

### 使用方式（buildTypes为debug时才可用）

1. @CollectSpentTime**Sync** 记录方法耗时（单一线程里的）

		@CollectSpentTimeSync(target = "初始化播放器耗时")
		private void initPlayer() {
			//TODO
	    }
		
		*********************通过广播发送信息给其他接收者*********************
		输出信息：
		target      测试的功能项
		spentTime   该功能耗时（ms）
		methodName  该功能在此方法上执行（如上面的initPlayer()）
		*******************************************************************

2. @CollectSpentTime**Async**  记录 复杂功能的执行完成的耗时（多线程里的，如调用网络数据到界面展示的整个过程，由主线程-子线程-主线程）
		
		记录功能耗时的起点
		@CollectSpentTimeAsync(target = "获取用户信息耗时")
		private void requestuserMsg() {
			//TODO
	    }

		功能执行完毕，结束记录
		@CollectSpentTimeAsync(target = "获取用户信息耗时", isEndPoint = true)   //结束记录的地方，要加上isEndPoint
		private void showUserMsg() {
			//TODO
	    }
		
		注：target的值必须保持一致

		*********************通过广播发送信息给其他接收者*********************
		输出信息：
		target      测试的功能项
		spentTime   该功能耗时（ms）
		methodName  该功能在此方法上执行（如 startName=requestuserMsg()，endName=showUserMsg()）
		*******************************************************************

3. @CollectCountMsg 统计某个功能是否执行正确

		@CollectCountMsg(target = "获取用户信息")
		private void showSuccessView() {
			//TODO		
		}

		*********************通过广播发送信息给其他接收者*********************
		输出信息：
		target       测试的功能项
		methodName   该功能在此方法上执行
		*******************************************************************

4. @CollectValueMsg 统计值

        @CollectValue(target = "获取计算值",parameterIndex = 0)
        private void calculate(float value){
            //TODO
        }

        注：1、parameterIndex用于指示所取的value是方法中的哪个参数
            2、@ValueParameter目前支持任意类型，最终所取得值为类型.toString()的返回值

        *********************通过广播发送信息给其他接收者*********************
        输出信息：
        target  测试的功能项
        value   该功能值
        *******************************************************************

5、@TagParameter 用于附带额外信息

        需要与@CollectSpentTimeSync、@CollectSpentTimeAsync、@CollectCountMsg、@CollectValueMsg配合使用

        @CollectCountMsg(target = "获取用户信息")
        private void showSuccessView(@TagParameter(name = "tag")String tag) {
            //TODO
        }

        注：1、方法中可多个参数同时使用@TagParameter
            2、@TagTagParameter目前支持任意类型，最终所取的值为类型.toString()的返回值

        *********************通过广播发送信息给其他接收者*********************
        输出信息：
        target       测试的功能项
        methodName   该功能在此方法上执行
        tag          <tag, xxx>
        *******************************************************************

### 接收方式
1. 以上信息通过广播发送给其他应用接收，action = applicationId + ".testmode.action"

		如：
		<receiver android:name=".TestBroadCast">
            <intent-filter>
                <action android:name="com.eebbk.uservoicecollection.testmode.action" />
            </intent-filter>
        </receiver>

		@Override
		public void onReceive(Context context, Intent intent) {
			String target = intent.getStringExtra("target");
			long spentTime = intent.getLongExtra("spentTime", 0);
			String methodName = intent.getStringExtra("methodName");
			String value = intent.getFloatExtra("value")
			String description = intent.getStringExtra("description");
			String tag = intent.getStringExtra("tag")
		}
		
2. 接收广播的应用可参考以下Demo：
[https://github.com/541205249/TestModeDemo](https://github.com/541205249/TestModeDemo)

3. 最终生成的测试结果：
![](https://i.imgur.com/FjHg54Z.png)
### 问题反馈
使用过程成遇到任何问题，有任何建议或者意见都可以使用下面这个地址反馈给我们。欢迎大家提出问题，我们将会在最短的时间内解决问题。
**https://github.com/541205249/TestModeTools/issues**

--------