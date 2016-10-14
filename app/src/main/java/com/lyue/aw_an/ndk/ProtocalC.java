package com.lyue.aw_an.ndk;

public class ProtocalC {
    static {
        System.loadLibrary("myJni");
    }

    public native String InitMulData();// 初始化

    public native boolean UnpackComData(int chan, int data);// 解析CTH协议数据，把接收到的数据作为参数传进去

    public native CthData GetBcgPackData(int type);// 获取解包后的数据

    public native void SetBcgProcFlag(int packType, int flag);

    public native int GetBcgProcFlag(int packType);

    public native CthData GetWifiPackData(int type);// 获取解包后的数据

    public native void SetWifiProcFlag(int packType, int flag);

    public native int GetWifiProcFlag(int packType);
    /*
	 * 往下发送命令
	 */

    public native void SendComPackChan();

    public native CthData ReadMulCycQue();//获取下发的指令,存放在byte[]中

    public native void InitStatData();// 睡眠分析初始化

    public native void StatSleepData(int hr, int rr, int motindex,
                                     int sigindex, int sigstate, int sigevent, int rrstate);

    public native int GetStatData(int type);

    //指令
    public native void SendWifiAt();//AT TEST

    public native void SendBcgCmdData(int org, int filt, int sim, int real, int sleep, int blue, int wifi);

    public native void SendWifiCwjap(int cnt, int flag, int no, int d1, int d2, int d3, int d4, int d5);

    public native void SendWifiCipstart(int key1, int key2, int key3, int key4, int port);

    public native void SendWifiRst();

    public native void SendWifiGmr();

    public native void SendWifiCipstatus();

    public native void SendWifiCifsr();

    public native void SendWifiCsysid();

    // 仿真波形
    public native void InitBcgSimData();

    public native int GetBcgSimhrWave(int hr, int state, int hrindex,
                                      int hrstate);

    public native int GetBcgSimrrWave(int rr, int state, int rrindex,
                                      int rrstate);

    // 包类型 packtype
    public static final int BCG_PACK_ORG = 0x00; // 原始波形
    public static final int BCG_PACK_FILT = 0x01; // 滤波波形
    public static final int BCG_PACK_SIM = 0x02; // 仿真波形
    public static final int BCG_PACK_BLUE = 0x03; // 蓝牙波形
    public static final int BCG_PACK_REAL = 0x04; // 实时数据
    public static final int BCG_PACK_SLEEP = 0x05; // 睡眠数据
    public static final int BCG_PACK_MID = 0x06; // MCU ID
    public static final int BCG_PACK_CTM = 0x07; // 软件编译时间
    public static final int BCG_PACK_VER = 0x08; // 软件版本号
    public static final int BCG_PACK_RTM = 0x09; // 上电运行时间
    public static final int BCG_PACK_CFG = 0x0a; // 模块配置信息
    public static final int BCG_PACK_CMDDATA = 0x0b; // 数据设置
    public static final int BCG_PACK_CMDUSER = 0x0c; // 用户设置
    public static final int BCG_PACK_CMDGAIN = 0x0d; // 增益设置
    public static final int BCG_PACK_CMDQRY = 0x0e; // 信息查询
    public static final int BCG_PACK_MAX = 0x0f;


    // 数据类型
    public enum BcgDataType {
        BCG_ORG_SIGNAL, // 测量信号波形
        BCG_ORG_MOTION, // 运动信号波形
        BCG_FILT_HEART, // 滤波心跳波形
        BCG_FILT_RESP, // 滤波呼吸滤波
        BCG_SIM_HEART, // 仿真心跳波形
        BCG_SIM_RESP, // 仿真呼吸波形
        BCG_BLUE_HEART1, // 心跳波形1
        BCG_BLUE_HEART2, // 心跳波形2
        BCG_BLUE_RESP1, // 呼吸波形1
        BCG_BLUE_RESP2, // 呼吸波形2
        BCG_REAL_HR, // 每分钟心跳次数
        BCG_REAL_RR, // 每分钟呼吸次数
        BCG_REAL_RRINDEX, // 呼吸强度指数
        BCG_REAL_HRINDEX, // 心跳强度指数
        BCG_REAL_MOTINDEX, // 运动强度指数
        BCG_REAL_SIGINDEX, // 信号强度指数
        BCG_REAL_SIGSTATE, // 测量信号状态
        BCG_REAL_SIGEVENT, // 测量信号事件
        BCG_REAL_RRSTATE, // 呼吸测量状态
        BCG_REAL_HRSTATE, // 心跳测量状态
        BCG_REAL_CPUTEMP, // 片内CPU温度
        BCG_SLEEP_HR, // 每分钟心跳次数
        BCG_SLEEP_RR, // 每分钟呼吸次数
        BCG_SLEEP_INDEX1, // 睡眠指数1
        BCG_SLEEP_INDEX2, // 睡眠指数2
        BCG_SLEEP_STATE, // 睡眠状态
        BCG_SLEEP_EVENT, // 睡眠事件
        BCG_MID_ID1, // CPU ID1
        BCG_MID_ID2, // CPU ID2
        BCG_MID_ID3, // CPU ID3
        BCG_MID_ID4, // CPU ID4
        BCG_MID_ID5, // CPU ID5
        BCG_MID_ID6, // CPU ID6
        BCG_CTM_YEAR, // 软件编译时间－年
        BCG_CTM_MONTH, // 软件编译时间－月
        BCG_CTM_DAY, // 软件编译时间－日
        BCG_CTM_HOUR, // 软件编译时间－时
        BCG_CTM_MINTUE, // 软件编译时间－分
        BCG_CTM_SECOND, // 软件编译时间－秒
        BCG_VER_SOFT, // 软件版本号
        BCG_VER_ALG, // 算法版本号
        BCG_RTM_YEAR, // 上电运行时间－年
        BCG_RTM_MONTH, // 上电运行时间－月
        BCG_RTM_DAY, // 上电运行时间－日
        BCG_RTM_HOUR, // 上电运行时间－时
        BCG_RTM_MINTUE, // 上电运行时间－分
        BCG_RTM_SECOND, // 上电运行时间－秒
        BCG_CFG_ORG, // 发送原始波形
        BCG_CFG_FILT, // 发送滤波波形
        BCG_CFG_SIM, // 发送仿真波形
        BCG_CFG_REAL, // 发送实时数据
        BCG_CFG_SLEEP, // 发送睡眠数据
        BCG_CFG_AGE, // 用户年龄
        BCG_CFG_TYPE, // 用户类型
        BCG_CFG_GAIN, // 硬件增益
        BCG_CFG_AUTOG, // 自动增益
        BCG_CMDDATA_ORG, // 发送原始波形
        BCG_CMDDATA_FILT, // 发送滤波波形
        BCG_CMDDATA_SIM, // 发送仿真波形
        BCG_CMDDATA_REAL, // 发送实时数据
        BCG_CMDDATA_SLEEP, // 发送睡眠数据
        BCG_CMDUSER_AGE, // 用户年龄
        BCG_CMDUSER_TYPE, // 用户类型
        BCG_CMDGAIN_GAIN, // 硬件增益
        BCG_CMDGAIN_AUTOG, // 自动增益
        BCG_CMDQRY_PID, // 查询ID
        BCG_DATA_MAX
    }

    public enum StatType {
        STAT_MAX_HR, // 心跳最大值
        STAT_MIN_HR, // 心跳最小值
        STAT_AVE_HR, // 心跳平均值
        STAT_MAX_RR, // 呼吸最大值
        STAT_MIN_RR, // 呼吸最小值
        STAT_AVE_RR, // 呼吸平均值
        STAT_OFFBED_CNT, // 离床次数
        STAT_APENA_CNT, // 呼吸暂停次数
        STAT_MOVE_CNT, // 体动次数
        STAT_ONBED_TIMES, // 在床时间,以秒为单位
        STAT_SLEEP_TIMES, // 入睡时间,以秒为单位
        STAT_TYPE_MAX
    }

    public static final int WIFI_PACK_RXDAT = 0; // WIFI串口接收数据
    public static final int WIFI_PACK_TXDAT = 1; // WIFI串口发送数据
    public static final int WIFI_PACK_NETDAT = 2; // WIFI串口网络数据
    public static final int WIFI_PACK_ACK = 3; // WIFI命令响应
    public static final int WIFI_PACK_STATUS = 4; // WIFI模块连接状态
    public static final int WIFI_PACK_MIP = 5; // WIFI模块IP地址
    public static final int WIFI_PACK_MAC = 6; // WIFI模块MAC地址
    public static final int WIFI_PACK_CMDSEND = 7; // 发送WIFI模块指令
    public static final int WIFI_PACK_CIPSEND = 8; // 发送WIFI模块网络数据
    public static final int WIFI_PACK_AT = 9; // 测试AT
    public static final int WIFI_PACK_RST = 10; // 复位重启
    public static final int WIFI_PACK_GMR = 11; // 版本信息
    public static final int WIFI_PACK_CWLAP = 12; // 扫描WIFI
    public static final int WIFI_PACK_CIPSTATUS = 13; // 连接状态
    public static final int WIFI_PACK_CWQAP = 14; // 断开WIFI
    public static final int WIFI_PACK_CIFSR = 15; // 模块IP
    public static final int WIFI_PACK_CSYSID = 16; // 模块ID
    public static final int WIFI_PACK_CWMODE = 17; // 设置工作模式
    public static final int WIFI_PACK_CWJAP = 18; // 连接WIFI
    public static final int WIFI_PACK_CWDHCP = 19; // 设置DHCP
    public static final int WIFI_PACK_CWAUTOCONN = 20; // 设置STA自动连接
    public static final int WIFI_PACK_CIPSTA = 21; // 设置STA IP地址
    public static final int WIFI_PACK_CIPSTAMAC = 22; // 设置STA MAC地址
    public static final int WIFI_PACK_CIPSTART = 23; // TCP连接服务器
    public static final int WIFI_PACK_MONDAT = 24; // 监测数据
    public static final int WIFI_PACK_REQDAT = 25; // 查询数据
    public static final int WIFI_PACK_MAX = 26;


    public enum WifiDataType {
        WIFI_RXDAT_CNT, // 数据个数
        WIFI_RXDAT_D1, // 接收字节1
        WIFI_RXDAT_D2, // 接收字节2
        WIFI_RXDAT_D3, // 接收字节3
        WIFI_RXDAT_D4, // 接收字节4
        WIFI_RXDAT_D5, // 接收字节5
        WIFI_TXDAT_CNT, // 数据个数
        WIFI_TXDAT_D1, // 发送字节1
        WIFI_TXDAT_D2, // 发送字节2
        WIFI_TXDAT_D3, // 发送字节3
        WIFI_TXDAT_D4, // 发送节4
        WIFI_TXDAT_D5, // 发送字节5
        WIFI_NETDAT_CNT, // 数据个数
        WIFI_NETDAT_D1, // 网络字节1
        WIFI_NETDAT_D2, // 网络字节2
        WIFI_NETDAT_D3, // 网络字节3
        WIFI_NETDAT_D4, // 网络字节4
        WIFI_NETDAT_D5, // 网络字节5
        WIFI_ACK_CMDID, // 命令ID
        WIFI_ACK_RESULT, // 执行结果
        WIFI_STATUS_VALUE, // 状态值
        WIFI_MIP_KEY1, // IP字段1
        WIFI_MIP_KEY2, // IP字段2
        WIFI_MIP_KEY3, // IP字段3
        WIFI_MIP_KEY4, // IP字段4
        WIFI_MAC_KEY1, // MAC字段1
        WIFI_MAC_KEY2, // MAC字段2
        WIFI_MAC_KEY3, // MAC字段3
        WIFI_MAC_KEY4, // MAC字段4
        WIFI_MAC_KEY5, // MAC字段5
        WIFI_MAC_KEY6, // MAC字段6
        WIFI_CMDSEND_CNT, // 数据个数
        WIFI_CMDSEND_FLAG, // 结束标志
        WIFI_CMDSEND_NO, // 指令组号
        WIFI_CMDSEND_D1, // 指令数据1
        WIFI_CMDSEND_D2, // 指令数据2
        WIFI_CMDSEND_D3, // 指令数据3
        WIFI_CMDSEND_D4, // 指令数据4
        WIFI_CMDSEND_D5, // 指令数据5
        WIFI_CIPSEND_CNT, // 数据个数
        WIFI_CIPSEND_FLAG, // 结束标志
        WIFI_CIPSEND_NO, // 指令组号
        WIFI_CIPSEND_D1, // 发送数据1
        WIFI_CIPSEND_D2, // 发送数据2
        WIFI_CIPSEND_D3, // 发送数据3
        WIFI_CIPSEND_D4, // 发送数据4
        WIFI_CIPSEND_D5, // 发送数据5
        WIFI_CWMODE_MODE, // 工作模式
        WIFI_CWJAP_CNT, // 数据个数
        WIFI_CWJAP_FLAG, // 结束标志
        WIFI_CWJAP_NO, // 参数组号
        WIFI_CWJAP_D1, // 参数数据1
        WIFI_CWJAP_D2, // 参数数据2
        WIFI_CWJAP_D3, // 参数数据3
        WIFI_CWJAP_D4, // 参数数据4
        WIFI_CWJAP_D5, // 参数数据5
        WIFI_CWDHCP_MODE, // 选择模式
        WIFI_CWDHCP_DHCP, // DHCP设置
        WIFI_CWAUTOCONN_FLAG, // 自动连接
        WIFI_CIPSTA_KEY1, // IP字段1
        WIFI_CIPSTA_KEY2, // IP字段2
        WIFI_CIPSTA_KEY3, // IP字段3
        WIFI_CIPSTA_KEY4, // IP字段4
        WIFI_CIPSTAMAC_KEY1, // MAC字段1
        WIFI_CIPSTAMAC_KEY2, // MAC字段2
        WIFI_CIPSTAMAC_KEY3, // MAC字段3
        WIFI_CIPSTAMAC_KEY4, // MAC字段4
        WIFI_CIPSTAMAC_KEY5, // MAC字段5
        WIFI_CIPSTAMAC_KEY6, // MAC字段6
        WIFI_CIPSTART_KEY1, // IP字段1
        WIFI_CIPSTART_KEY2, // IP字段2
        WIFI_CIPSTART_KEY3, // IP字段3
        WIFI_CIPSTART_KEY4, // IP字段4
        WIFI_CIPSTART_PORT, // 端口号
        WIFI_MONDAT_RXDAT, // 接收数据
        WIFI_MONDAT_TXDAT, // 发送数据
        WIFI_MONDAT_NETDAT, // 网络数据
        WIFI_REQDAT_CMDID, // 查询ID
        WIFI_DATA_MAX
    }

        public static final int WIFI_RXDAT_ID = 0x01;	//WIFI串口接收数据
        public static final int WIFI_TXDAT_ID = 0x02;		//WIFI串口发送数据
        public static final int WIFI_NETDAT_ID = 0x03;		//WIFI串口网络数据
        public static final int WIFI_ACK_ID = 0x04;	//WIFI命令响应
        public static final int WIFI_STATUS_ID = 0x05;		//WIFI模块连接状态
        public static final int WIFI_MIP_ID = 0x06;		//WIFI模块IP地址
        public static final int WIFI_MAC_ID = 0x07;		//WIFI模块MAC地址
        public static final int WIFI_CMDSEND_ID = 0x81;		//发送WIFI模块指令
        public static final int WIFI_CIPSEND_ID = 0x82;		//发送WIFI模块网络数据
        public static final int WIFI_AT_ID = 0x83;		//测试AT
        public static final int WIFI_RST_ID = 0x84;		//复位重启
        public static final int WIFI_GMR_ID = 0x85;		//版本信息
        public static final int WIFI_CWLAP_ID = 0x86;		//扫描WIFI
        public static final int WIFI_CIPSTATUS_ID = 0x87;		//连接状态
        public static final int WIFI_CWQAP_ID = 0x88;		//断开WIFI
        public static final int WIFI_CIFSR_ID = 0x89;		//模块IP
        public static final int WIFI_CSYSID_ID = 0x8A;		//模块ID
        public static final int WIFI_CWMODE_ID = 0x91;		//设置工作模式
        public static final int WIFI_CWJAP_ID = 0x92;		//连接WIFI
        public static final int WIFI_CWDHCP_ID = 0x93;		//设置DHCP
        public static final int WIFI_CWAUTOCONN_ID = 0x94;		//设置STA自动连接
        public static final int WIFI_CIPSTA_ID = 0x95;		//设置STA IP地址
        public static final int WIFI_CIPSTAMAC_ID = 0x96;		//设置STA MAC地址
        public static final int WIFI_CIPSTART_ID = 0x97;		//TCP连接服务器
        public static final int WIFI_MONDAT_ID = 0xA1;		//监测数据
        public static final int WIFI_REQDAT_ID = 0xA2;		//查询数据
}
