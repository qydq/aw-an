package com.lyue.aw_an.ndk;


import java.util.Arrays;

public class SleepUnpack {
    public static String recFromWifi = "";
    public static ProtocalC protocalC = new ProtocalC();
    static StringBuilder bufBuilder = new StringBuilder();
    static StringBuilder bufBuilder2 = new StringBuilder();
    public static boolean isWifiConfiging;
    public static boolean isWifiConnected;
    public static boolean isServerConnected;
    public static int serverFailCnt;
    public static int wifiFailCnt;

    public static int signalEvent;
    public static int signalState;
    public static int hrState;
    public static int rrState;
    public static int hrIndex;
    public static int rrIndex;
    public static int motindex;
    public static int sigindex;
    public static int realHr;
    public static int realRr;
    public static boolean isWorking;
    public static int filterHrWave1 = 2048;
    public static int filterHrWave2 = 2048;
    public static int filterRrWave1 = 2048;
    public static int filterRrWave2 = 2048;

    public static boolean isDeviceConnected = false;
    public static boolean isOnbed = false;
    public static boolean isMov = false;
    public static CircleBuf hrWaveBuf = new CircleBuf();
    public static CircleBuf rrWaveBuf = new CircleBuf();
    static public int mConnStatus = 0;
    public static int waveMode = 0;
    public static int bluetoothWaveType = 0;

    public static int WAVE_TYPE_SIMULATE = 0;// 仿真波形
    public static int WAVE_TYPE_FILTER = 1;// 原始波形

    public static void getBluetoothData() {
        int i, flag = 0;
//         MyPrint.print("protimetask");
        for (i = 0; i < ProtocalC.BCG_PACK_MAX; i++) {
            flag = protocalC.GetBcgProcFlag(i);
//				MyPrint.print("flag...."+flag+"...i...."+i);
            if (flag > 0) {
//                MyPrint.print("flag...."+flag+"...i...."+i);
                CthData data;
                flag = 0;
                switch (i) {
                    case ProtocalC.BCG_PACK_REAL:
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_HR
                                        .ordinal());
                        realHr = data.value == 255 ? 0 : data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_RR
                                        .ordinal());
                        realRr = data.value == 255 ? 0 : data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_SIGSTATE
                                        .ordinal());
                        signalState = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_SIGEVENT
                                        .ordinal());
                        signalEvent = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_SIGINDEX
                                        .ordinal());
                        sigindex = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_HRSTATE
                                        .ordinal());
                        hrState = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_RRSTATE
                                        .ordinal());
                        rrState = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_HRINDEX
                                        .ordinal());
                        hrIndex = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_RRINDEX
                                        .ordinal());
                        rrIndex = data.value;
                        data = protocalC
                                .GetBcgPackData(ProtocalC.BcgDataType.BCG_REAL_MOTINDEX
                                        .ordinal());
                        motindex = data.value;

                        // 1:搜索信号 2:无信号 3:有信号
                        isDeviceConnected = true;
                        if (signalState == 3) {
                            isOnbed = true;
                        } else {
                            if (isOnbed) {
                                // 上一状态是在床,现在离床了
                                isOnbed = false;
                                SleepSta.offBedCnt++;
                            }
                        }
                        protocalC.StatSleepData(realHr, realRr, motindex,
                                sigindex, signalState, signalEvent, rrState);
//                        realDataBluetoothHandler.sendEmptyMessage(0);// 刷新显示数值
                        break;
                    case ProtocalC.BCG_PACK_BLUE:
                        // 滤波波形
                        if (bluetoothWaveType == WAVE_TYPE_FILTER) {
                            data = protocalC
                                    .GetBcgPackData(ProtocalC.BcgDataType.BCG_BLUE_HEART1
                                            .ordinal());
                            filterHrWave1 = data.value;
                            hrWaveBuf.put(filterHrWave1);
                            data = protocalC
                                    .GetBcgPackData(ProtocalC.BcgDataType.BCG_BLUE_HEART2
                                            .ordinal());
                            filterHrWave2 = data.value;
                            hrWaveBuf.put(filterHrWave2);
                            data = protocalC
                                    .GetBcgPackData(ProtocalC.BcgDataType.BCG_BLUE_RESP1
                                            .ordinal());
                            filterRrWave1 = data.value;
                            rrWaveBuf.put(filterRrWave1);
                            data = protocalC
                                    .GetBcgPackData(ProtocalC.BcgDataType.BCG_BLUE_RESP2
                                            .ordinal());
                            filterRrWave2 = data.value;
                            rrWaveBuf.put(filterRrWave2);
                        }
                        break;
                }
                protocalC.SetBcgProcFlag(i, flag);
            }
        }

    }

    public static void getWifiData() {
        int flag = 0;
        for (int i = 0; i < ProtocalC.WIFI_PACK_MAX; i++) {
            flag = protocalC.GetWifiProcFlag(i);
            if (flag > 0) {
                CthData data;
                flag = 0;
                int tmpLen = 0;
                int len;
                byte[] recDat = new byte[5];
                switch (i) {
                    case ProtocalC.WIFI_PACK_RXDAT:
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_CNT
                                        .ordinal());
                        len = data.value;
                        if (len <= 2) {
                            break;// \r\n
                        }
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_D1
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_D2
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_D3
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_D4
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_RXDAT_D5
                                        .ordinal());
                        recDat[tmpLen] = (byte) data.value;
                        recDat = Arrays.copyOfRange(recDat, 0, len);
                        // MyPrint.print("WIFI_RXDAT........." +
                        // MyUtil.byteArrayToString(recDat));

                        bufBuilder.append(MyUtil.byteArrayToString(recDat));

                        break;
                    case ProtocalC.WIFI_PACK_TXDAT:

                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_CNT
                                        .ordinal());
                        len = data.value;
                        if (len <= 2) {
                            break;// \r\n
                        }
                        // MyPrint.print("WIFI_TXDAT_CNT....." + data.value);

                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_D1
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_D2
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_D3
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_D4
                                        .ordinal());
                        recDat[tmpLen++] = (byte) data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_TXDAT_D5
                                        .ordinal());
                        recDat[tmpLen] = (byte) data.value;
                        recDat = Arrays.copyOfRange(recDat, 0, len);
                        // MyPrint.print("WIFI_TXDAT........." +
                        // MyUtil.byteArrayToString(recDat));
                        // MyPrint.print("recTxdata.hex.."+MyUtil.ByteArrayToHex(recDat));
                        bufBuilder2.append(MyUtil.byteArrayToString(recDat));
                        break;

                    case ProtocalC.WIFI_PACK_AT:
                        break;
                    case ProtocalC.WIFI_PACK_CWJAP:
//                        MyPrint.print("连接wifi");
                        break;
                    case ProtocalC.WIFI_PACK_CIPSTART:
//                        MyPrint.print("连接服务器");
                        break;
                    case ProtocalC.WIFI_PACK_STATUS:

                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_STATUS_VALUE
                                        .ordinal());
                        mConnStatus = data.value;
                        switch (mConnStatus) {
                            //wifi连接状态 1：正在配置中  3:连接正常 4:连接服务器失败 5：wifi连接失败
                            case 1:
                                //正在初始化
                                serverFailCnt = 0;
                                wifiFailCnt = 0;
                                isWifiConfiging = true;
                                isWifiConnected = false;
                                isServerConnected = false;
                                break;
                            case 2:
                                //连上wifi
//                                isWifiConfiging = false;
//                                isWifiConnected = true;
//                                isServerConnected = false;
                                break;
                            case 3:
                                //一切正常
                                isWifiConfiging = false;
                                isWifiConnected = true;
                                isServerConnected = true;
                                break;
                            case 4:
                                //服务器断开
                                serverFailCnt++;
                                isWifiConfiging = false;
                                isWifiConnected = true;
                                isServerConnected = false;
                                break;
                            case 5:
                                //没连上wifi，服务器也没连上
                                wifiFailCnt++;
                                isWifiConfiging = false;
                                isWifiConnected = false;
                                isServerConnected = false;
                                break;
                        }
                        break;
                    case ProtocalC.WIFI_PACK_ACK:
                        //ackFlag  0:失败 1:成功 2:繁忙 3:超时
                        int cmdId, ackFlag;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_ACK_CMDID
                                        .ordinal());
                        cmdId = data.value;
                        data = protocalC
                                .GetWifiPackData(ProtocalC.WifiDataType.WIFI_ACK_RESULT
                                        .ordinal());
                        ackFlag = data.value;

                        if (cmdId == ProtocalC.WIFI_CIPSEND_ID) {

                        }
                        if (cmdId == ProtocalC.WIFI_CIPSTATUS_ID) {

                        }
                        break;
                }
                protocalC.SetWifiProcFlag(i, flag);
            }
//            getPackFromSendQue();

        }

    }


    public static void getSleepData() {
        int flag = 0;
        for (int i = 0; i < ProtocalC.BCG_PACK_MAX; i++) {
            flag = protocalC.GetBcgProcFlag(i);
            if (flag > 0) {
                // MyPrint.print("getSleepData...." + flag + "...i...." + i);
            }
        }
    }


    public static byte[] getPackFromSendQue() {
        CthData cmdData = protocalC.ReadMulCycQue();
        return cmdData.cmd;
    }

    public static byte[] getWifiCfgCmdFromJni(byte[] cmd, int cnt, int flag,
                                              int no) {
        byte[] tmp = new byte[5];
        for (int i = 0; i < cmd.length; i++) {
            tmp[i] = cmd[i];
        }
        //
        protocalC.SendWifiCwjap(cnt, flag, no, tmp[0], tmp[1], tmp[2], tmp[3],
                tmp[4]);
        CthData cmdData = protocalC.ReadMulCycQue();
        return cmdData.cmd;
    }

    public static void clearData() {
        signalEvent = 0;
        signalState = 0;
        hrState = 0;
        rrState = 0;
        hrIndex = 0;
        rrIndex = 0;
        motindex = 0;
        sigindex = 0;
        realHr = 0;
        realRr = 0;
        isWorking = false;
    }

}
