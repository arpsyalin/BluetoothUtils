package com.lyl.bluetoothutils.spp;

/**
 * * @Description UUID列表
 * * @Author 刘亚林
 * * @CreateDate 2020/11/13
 * * @Version 1.0
 * * @Remark TODO
 **/
public class CONSTANT_UUID {
    public static final String DEFAULT = "00001101-0000-1000-8000-00805F9B34FB";
    //蓝牙发现服务
    public static final String Service_Discovery_Server_ServiceClassID_UUID = "00001000-0000-1000-8000-00805F9B34FB";
    ///
    public static final String Browse_Group_Descriptor_ServiceClassID_UUID = "00001001-0000-1000-8000-00805F9B34FB";
    public static final String Public_Browse_Group_ServiceClass_UUID = "00001002-0000-1000-8000-00805F9B34FB";
    //蓝牙串口服务
    public static final String Serial_Port_Service_Class_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    //拨号网络服务
    public static final String Dial_Up_Networking_ServiceClass_UUID = "00001103-0000-1000-8000-00805F9B34FB";
    //信息同步服务
    public static final String IrMC_Sync_ServiceClass_UUID = "00001104-0000-1000-8000-00805F9B34FB";
    public static final String SDP_OBEX_Object_Push_ServiceClass_UUID = "00001105-0000-1000-8000-00805F9B34FB";
    //文件传输服务
    public static final String OBEX_File_Transfer_ServiceClass_UUID = "00001106-0000-1000-8000-00805F9B34FB";
    //信息同步命令服务类
    public static final String IrMC_Sync_Command_ServiceClass_UUID = "00001107-0000-1000-8000-00805F9B34FB";
    //耳机服务类
    public static final String SDP_Headset_ServiceClass_UUID = "00001108-0000-1000-8000-00805F9B34FB";

    //---------------- A2DP
    //音频源服务
    public static final String Audio_Source_ServiceClass_UUID = "0000110A-0000-1000-8000-00805F9B34FB";
    //音频接收器服务类
    public static final String Audio_Sink_ServiceClass_UUID = "0000110B-0000-1000-8000-00805F9B34FB";
    //---------------- A2DP

    //高级音频分发服务
    public static final String Advanced_Audio_Distribution_ServiceClass_UUID = "0000110D-0000-1000-8000-00805F9B34FB";

    //---------------- AVRCP
    //远程遥控器目标服务
    public static final String AV_Remote_Control_Target_ServiceClass_UUID = "0000110C-0000-1000-8000-00805F9B34FB";
    //音视频远程控制服务
    public static final String AV_Remote_Control_ServiceClass_UUID = "0000110E-0000-1000-8000-00805F9B34FB";
    //音频和视频遥控器服务
    public static final String AV_Remote_Control_Controller_ServiceClass_UUID = "0000110F-0000-1000-8000-00805F9B34FB";
    //---------------- AVRCP

    //耳机音频网关服务类
    public static final String Headset_Audio_Gateway_ServiceClass_UUID = "00001112-0000-1000-8000-00805F9B34FB";
    //个人局域网服务
    public static final String PANUServiceClass_UUID = "00001115-0000-1000-8000-00805F9B34FB";
    //个人局域网服务
    public static final String NAPServiceClass_UUID = "00001116-0000-1000-8000-00805F9B34FB";
    //个人局域网服务 Personal Area Networking Profile (PAN)
    public static final String GN_ServiceClass_UUID = "00001117-0000-1000-8000-00805F9B34FB";

    //---------------- BIP 0x0200,0x0310-0x0313
    public static final String Imaging_ServiceClass_UUID = "0000111A-0000-1000-8000-00805F9B34FB";
    public static final String Imaging_Responder_ServiceClass_UUID = "0000111B-0000-1000-8000-00805F9B34FB";
    public static final String Imaging_Automatic_Archive_ServiceClass_UUID = "0000111C-0000-1000-8000-00805F9B34FB";
    public static final String Imaging_Referenced_Objects_ServiceClass_UUID = "0000111D-0000-1000-8000-00805F9B34FB";
    //----------------- BIP
    /**
     * 免提模式服务
     * Hands-Free Profile (HFP)
     * NOTE: Used as both Service Class Identifier and Profile Identifier.
     */
    public static final String Hands_Free_ServiceClass_UUID = "0000111E-0000-1000-8000-00805F9B34FB";
    //免提音频网关服务
    public static final String Hands_Free_Audio_Gateway_ServiceClass_UUID = "0000111F-0000-1000-8000-00805F9B34FB";
    //---------------- BPP Attribute 0x0350——0x037A step=2 属性描述含义
    //直接打印服务
    public static final String Direct_Printing_ServiceClass_UUID = "00001118-0000-1000-8000-00805F9B34FB";
    //参考打印服务
    public static final String Reference_Printing_ServiceClass_UUID = "00001119-0000-1000-8000-00805F9B34FB";
    //直接打印参考对象服务
    public static final String Direct_Printing_Reference_ObjectsServiceClass_UUID = "00001120-0000-1000-8000-00805F9B34FB";
    //直接打印参考对象服务
    public static final String Reflected_UI_ServiceClass_UUID = "00001121-0000-1000-8000-00805F9B34FB";
    //基本打印服务
    public static final String Basic_Printing_ServiceClass_UUID = "00001122-0000-1000-8000-00805F9B34FB";
    //打印状态服务
    public static final String Printing_StatusServiceClass_UUID = "00001123-0000-1000-8000-00805F9B34FB";
    //----------——————— BPP
    /**
     * 人机输入服务
     * Human Interface Device (HID)
     * NOTE: Used as both Service Class Identifier and Profile Identifier.
     */
    public static final String Human_Interface_Device_ServiceClass_UUID = "00001124-0000-1000-8000-00805F9B34FB";
    //----------——————— HCRP Hardcopy Cable Replacement Profile (HCRP) //蓝牙HCR打印服务
    public static final String Hard_Copy_Cable_Replacement_ServiceClass_UUID = "00001125-0000-1000-8000-00805F9B34FB";
    public static final String HCR_Print_ServiceClass_UUID = "00001126-0000-1000-8000-00805F9B34FB";
    public static final String HCR_Scan_ServiceClass_UUID = "00001127-0000-1000-8000-00805F9B34FB";
    //----------——————— HCRP

    //SAP
    public static final String SIM_Access_ServiceClass_UUID = "0000112D-0000-1000-8000-00805F9B34FB";
    //Device Identification (DID)
    public static final String PnP_Information_ServiceClass_UUID = "00001200-0000-1000-8000-00805F9B34FB";

    //----------——————— N/A
    //通用网络服务
    public static final String Generic_Networking_ServiceClass_UUID = "00001201-0000-1000-8000-00805F9B34FB";
    //通用文件传输服务
    public static final String Generic_File_Transfer_ServiceClass_UUID = "00001202-0000-1000-8000-00805F9B34FB";
    //通用音频服务
    public static final String Generic_Audio_ServiceClass_UUID = "00001203-0000-1000-8000-00805F9B34FB";
    //通用电话服务
    public static final String Generic_Telephony_ServiceClass_UUID = "00001204-0000-1000-8000-00805F9B34FB";
    //----------——————— N/A
}
