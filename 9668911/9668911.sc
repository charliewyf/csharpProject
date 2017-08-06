//增加初始变量声明
#iniparam int  @MaxRecordTime
#iniparam string @vox_PromptPath
#iniparam string @vox_RecordPath
#iniparam string @vox_MusicPath
#iniparam string @vox_Music
#iniparam string @vox_failInsDB
#iniparam string @vox_dy
#iniparam string @vox_dy00
#iniparam string @vox_dy11
#iniparam string @vox_cntxx
#iniparam string @vox_cntxy
#iniparam string @vox_cntfail
#iniparam string @vox_break
#iniparam string @LakeTable
#iniparam string @Vox_cantrans
#iniparam string @vox_dy00b
#iniparam string @vox_dy11b
#iniparam string @vox_dyLR
#iniparam string @serviceList
#iniparam string @noVipNDay
#iniparam string @OnlineUserTable
#iniparam int @VIPYouxianCon
#iniparam int @noVipPerDayCon
#iniparam int @noVipNDayCon

#include "H:\subfunc\rount.sub"

//增加函数声明
#function destoryflow
#function destoryflowa
#function CheckLaKetel
#function GetMusicPath
#function judgeTodayConCount


#function main

main
{
:stWait
    Connect->stAllocMedia
:stAllocMedia
    [ AllocMediaDevice 1 ]
    AllocResourceOK->stDeleteDeviceRepeat
    AllocResourceFail->stExit

:stDeleteDeviceRepeat
	[ ClearDTMF ]
    [ Assign SR1,"delete "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where DEVICEID=" ]
    [ ItoS $Device1 ]
    [ Strcat SR1,SR0 ]
    [ ExecSQL SR1 ]
    Passed->stBillBegin
    Failed->stBillBegin
    TimeOut->stBillBegin

:stBillBegin
    [ NowTime ]
    [ Assign IR28,IR0 ]//IR28 save the start time of this thread, **don't update it
    [ Assign IR4,0 ]	//IR4 save the user connect status, 0-free, others-busy:save friend deviceID
    [ OnCallCleared  &destoryflow ]


    [ SetDBTimeOut 30 ]                 //dawei add 2003.11.12 22:20
    [ BillBegin 1,0,$ServiceItemNo ]
    Verify(0)  -> stJudgeSex
    Verify(-1) -> stExit

:stJudgeSex
    [ StrSub $CalledID,6,6 ]      //实际使用
    [ Assign SR2,SR0 ]
    [ Equal SR2,"0" ]   //所有以“966890X”开始的号码都判断为女士,计费时免费
    Passed->stPlayMianFei//0X女士
    Failed->stJudge966899//判断是否9XX

:stJudge966899              //移动966899XXX资费为：0.09元，所以不接入该号段。提示用户更改号码
    [ Equal SR2,"9" ]
    Passed->stPlayChangeCalled
    Failed->stJudge966892224//判断是否22等原来宣传的女士号码

:stJudge966892224
    [ StrStr "$9668922$9668924$",$CalledID ]      //判断女士是否是22，是则播放"本号码已变更为9668900。。。"不是则到男士
    [ Great IR0,0 ]
    Passed->stPlayChangeCalled
    Failed->stJudgeVipFrom003//11男士

:stPlayChangeCalled
    [ PlayFile "h:\vox\bg00.vox" ]
    StopPlayBack->stPlayChangeCalled2
    DialTerminate->stPlayChangeCalled2
    Failed->stPlayChangeCalled2

:stPlayChangeCalled2
    [ PlayFile "h:\vox\bg00.vox" ]
    StopPlayBack->stExit
    DialTerminate->stExit
    Failed->stExit

:stPlayMianFei
    [ PlayFile "h:\vox\mianfei.VOX" ]
    StopPlayBack->stParaminput
    DialTerminate->stParaminput
    Failed->stParaminput

:stAlert    //20080716add报警防止无法访问H盘
    [ Assign SR1,"insert into T_PREDIAL values('3c2djtfm7q4',0,'02988239668'" ]
    [ Strcat SR1,",'02913991974000','9668951899',0,'2008-07-16 17:59:53','2008-07-16 17:59:53'" ]
    [ Strcat SR1,",20,'1^h:\Record\temp\02988239668_3c2djtfm7q4.tmp^d:\gzesun\宋祖英-辣妹子.vox^20080716175953^'" ]
    [ Strcat SR1,",'168',1,0,31,'96689000')"]
    [ ExecSQL SR1 ]
    Passed->stExit
    Failed->stExit
    TimeOut->stExit

:stJudgeVipFrom003
      [ StrSub $InputParam,1,8 ]
      [ Equal SR0,"96689003" ]    //vip user not playfee 20150303
      Passed->stSetVipFrom003
      Failed->stJudgeVipBoda11

:stSetVipFrom003
	[Assign SR17,"1" ]//SR17 save vip status. '1' from 003 vip, else others
	->stJudgeForLR

//20150112判断是否VIP直接拨打11-start
:stJudgeVipBoda11
   [Assign SR17,"0" ]
   [ Assign SR1,"select 1 from T_vipuid where VIPUID='" ]
   [ Strcat SR1,$CallerID ]
   [ Strcat SR1,"' and convert(datetime,STARTDATE)<=getdate() and convert(datetime,STOPDATE+' 23:59:59')>getdate()" ]
   [ ExecSQL SR1 ]
    Passed->stCheckvip
    Failed->stJudgeUserTodayConCount
    Timeout->stJudgeUserTodayConCount

:stCheckvip
    [ Great $DBRecCount,0 ]
    Passed->stPlayVipbo003
    Failed->stJudgeUserTodayConCount

:stPlayVipbo003
    [ ClearDTMF ]
    [ PlayFile ".\vox\vipbo003.vox" ]
     StopPlayBack->stExit
    DialTerminate->stExit
    Failed->stExit

:stJudgeUserTodayConCount
    [ CallFunc &judgeTodayConCount ]
    Returned(0)->stBeforeExit
    Returned->stPlayFeelv

//20150112判断是否VIP直接拨打11-end

:stPlayFeelv
    [ CallFunc &PlayFee ]
    Returned(0)->stJudgeForLR
    Returned->stParaminput

:stJudgeForLR
    [ StrStr "$96689169$96689189$9668982",$CalledID ]
    [ Great IR0,0 ]
    Passed->stPlayForLR    //Passed->stParaminput
    Failed->stParaminput

:stPlayForLR                        
    [ ClearDTMF ]
    [ SetDTMF "12" ]
    [ PlayFile ".\vox\lrjydhw.VOX" ] //判断是否需要选择男女
    StopPlayBack->stGetChoiceForLR
    DialTerminate->stGetChoiceForLR
    Failed->stGetChoiceForLR
    CallTerm->stExit

:stGetChoiceForLR
    [ Digit 5 ]
    StopDigits('1')->stBeforeInitMale
    StopDigits('2')->stBeforeInitFemale
    StopDigits->stPlayForLR
    TimeOut->stPlayForLR

:stBeforeInitMale
    [ Assign SR2,"0" ]
    ->stParaminput

:stBeforeInitFemale
    [ Assign SR2,"1" ]
    ->stParaminput

:stParaminput
    [ Equal $InputParam,"" ]
    Passed->stParaminput2
    Failed->stParaminput1

:stParaminput1
    [ ASSIGN $BillParam1,$InputParam ]   //一号通
    ->stInitParam

:stParaminput2
    [ ItoS $Device1 ]
    [ Assign SR19,SR0 ]           
    [ NowTime ]
    [ Strcat SR19,SR0 ]
    [ ASSIGN $BillParam1,SR19 ]   //一号通
    ->stInitParam

:stInitParam
    [ Assign IR25,0 ]    //是否已双向录音，0，未
    [ Assign IR26,0 ]   //已对接次数
    [ Assign IR27, @MaxRecordTime ]
    [ IDiv  IR27,10 ]//录音次数,每次10秒
    [ Assign IR21,0 ]   //朋友索引
	->stJudgeVipPriority

//20150109增加，vip用户默认优先接通1次
:stJudgeVipPriority
      [ Equal SR17,"1" ]
      Passed->setVipPriority
      Failed->stContineGetSex

:setVipPriority
    [ Assign IR26,@VIPYouxianCon ]
    ->stContineGetSex

:stContineGetSex
    [ Equal SR2,"0" ]
    Passed->stInitFemale
    Failed->stJudgeGMIC

:stJudgeGMIC
     [ CallFunc &JudgeGMIC ]           //判断是否为公免电话或IC卡电话
     Returned(1)->stExit
     Returned->stInitMale

:stInitFemale
    [ ASSIGN IR2,0 ]           //女
    [ ASSIGN IR3,1 ]           //找男
    -> stChecktel

:stInitMale
    [ ASSIGN IR2,1 ]           //男
    [ ASSIGN IR3,0 ]           //找女
    -> stClearDB

//检查是否为拉客库中的电话
:stChecktel
   [ CallFunc &CheckLaKetel ]
   Returned(0)->stExit
   Returned->stClearDB

//初始化数据结束，开始入库
:stClearDB                      //删除相同device的记录
    [ ASSIGN SR1,"delete  "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stAddToDB
    Failed ->stAddToDB
    TimeOut ->stAddToDB

:stAddToDB                           //插入本device
    [ ASSIGN SR1,"insert into  "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," values(" ]
    [ ItoS $Device1 ]
    [ STRCAT SR1,SR0 ]            //device
    [ STRCAT SR1,"," ]
    [ ItoS IR2 ]
    [ STRCAT SR1,SR0 ]           //sex
    [ STRCAT SR1,",0,'" ]          //status
    [ Strcat SR1,$CallerID ]      //USERCODE
    [ Strcat SR1,"','" ]
    [ NowTime ]
    [ Strcat SR1,SR0 ]             //LOGINDT
    [ Strcat SR1,"',''," ]          //CONNECTDT   
    [ ItoS IR26 ]
    [ Strcat SR1,SR0 ]             //CONNECTCOUNT
    [ Strcat SR1,")" ]
    
    [ EXECSQL SR1 ]
    Passed ->stPlayWarning
    Failed ->stPlayFailInsDB
    TimeOut ->stPlayFailInsDB

:stPlayFailInsDB
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_failInsDB ]    //$添加数据库记录失败
    [ PlayFile SR1]
    StopPlayBack->stExit
    DialTerminate->stExit
    Failed->stExit

:stPlayWarning
    [ ClearDTMF ]
    ->stCheckFriend

:stCheckFriend               //检查是否有在线朋友，对方挂机后也返回到这里。
    [ Equal IR2,0 ]
    Passed->stSearchMale  //女找男
    Failed->stSearchFemale//男找女

:stSearchMale
    [ ASSIGN SR1,"select DEVICEID,USERCODE from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where STATUS=0 and SEX=1" ]
    //[ Strcat SR1," and  (charindex(substring(USERCODE,4,3),'" ]
    //[ Strcat SR1,"134,135,136,137,138,139,150,151,152,158,159,182,183,187,188,133,153,189,029,091"]
    //[Strcat SR1,"')>0 OR charindex(substring(USERCODE,1,4),'0298,0293')>0)" ]
    [ Strcat SR1," order by CONNECTCOUNT,LOGINDT" ]
    [ EXECSQL SR1 ]
    Passed ->stCheckMaleCount
    Failed ->stWelcome
    TimeOut ->stWelcome

:stCheckMaleCount
    [ Great $DBRecCount,0 ]
    Passed->stFoundAFriend
    Failed->stWelcome

:stSearchFemale
    [ ASSIGN SR1,"select DEVICEID,USERCODE from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where SEX=0" ]
    [ STRCAT SR1," and STATUS=0  and USERCODE NOT IN (SELECT CALLERID FROM T_LakeTel) " ]
    [ Strcat SR1," order by  CONNECTCOUNT,LOGINDT" ]

    [ EXECSQL SR1 ]
    Passed ->stCheckFemaleCount
    Failed ->stWelcome
    TimeOut ->stWelcome

:stCheckFemaleCount
    [ Great $DBRecCount,0 ]
    Passed->stFoundAFriend
    Failed->stWelcome

:stFoundAFriend
   [ Assign IR21,0 ]

    [ StoI FD0 ]
    [ ASSIGN IR4,IR0 ]         //取对方标识
    [ Assign SR11,FD1 ]        //取对方主叫
    [ ASSIGN SR1,"update "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," set STATUS=" ]   //更新状态
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]                       //将对方device放入状态标识中
    [ Strcat SR1,",CONNECTDT='"]
    [ NowTime ]
    [ Strcat SR1,SR0]               //当前连接时间
    [ Strcat SR1,"',CONNECTCOUNT="]

    [ Inc IR26 ]
    [ ItoS IR26 ]
    [ Assign $BillParam2,SR0 ]

    [ ItoS IR26 ]
    [ Strcat SR1,SR0 ]               //当前连接次数
    [ STRCAT SR1," where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stPutMessage
    Failed ->stConnectFail
    TimeOut ->stConnectFail
:stConnectFail
    [ ASSIGN SR1,@vox_PromptPath] 
    [ STRCAT SR1,@vox_cntfail]
    [ PlayFile SR1]
    StopPlayBack->stCheckFriend
    DialTerminate->stCheckFriend
    Failed->stCheckFriend

:stPutMessage            //向对方device发送消息
    [ ItoS $Device1 ]
    [ PutMessage IR4,IR2,SR0 ]
    ->stCheckSexForConnect

:stWelcome
    [ Equal IR2,0]
    Passed->stWelcome00    //女
    Failed->stWelcome11    //男

:stWelcome00                   
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_dy00 ]            //有人说，朋友就是。。。
    [ PlayFile SR1 ]
    StopAction->stWelcome00

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stStartPlayMusic
    DialTerminate->stStartPlayMusic
    Failed->stStartPlayMusic

:stWelcome11                   //有人说，朋友就是。。。
    [ ASSIGN SR1,@vox_PromptPath] 
    [ STRCAT SR1,@vox_dy11]
    [ PlayFile SR1]
    StopAction->stWelcome11
    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stStartPlayMusic
    DialTerminate->stStartPlayMusic
    Failed->stStartPlayMusic

//随机播放音乐前,判断该用户是否本月超时,是则听音乐,不是则播放语音:*号键可以和本台热线小姐交流
:stStartPlayMusic
     [ ClearDTMF ]
    ->stGetRandomMusic

:stGetRandomMusic
    [ ClearDTMF ]
    [ SetDTMF "?" ]

    [ CallFunc &GetMusicPath ] 
    Returned->stPlayMusicNow

:stPlayMusicNow
    [ ClearDTMF ]
    [ SetDTMF "#" ]
    [ PlayFile SR1 ]
    StopAction->stCheckFriend

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt

    StopPlayBack->stGetRandomMusic
    DialTerminate->stGetRandomMusic
    Failed->stGetRandomMusic

:stStopVox
    [ StoI $InputParam ]
    [ ASSIGN IR4,IR0 ]
    ->stSetDBCntOK

:stSetDBCntOK
    [ Inc IR26 ]
    [ ItoS IR26 ]
    [ Assign $BillParam2,SR0 ]

    [ ASSIGN SR1,"update "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," set STATUS=" ]   //更新状态
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]                       //将对方device放入状态标识中
    [ Strcat SR1,",CONNECTDT='"]
    [ NowTime ]
    [ Strcat SR1,SR0]               //当前连接时间
    [ Strcat SR1,"',CONNECTCOUNT="]
    [ ItoS IR26 ]
    [ Strcat SR1,SR0]               //当前连接次数
    [ STRCAT SR1," where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ STRCAT SR1,"'" ]

    [ EXECSQL SR1 ]
    Passed ->stStopVoxAction
    Failed ->stStopVoxAction
    TimeOut ->stStopVoxAction

:stStopVoxAction
    [ StopVoxAction ]
    StopAction->stCheckSexForConnect

:stCheckSexForConnect
    [ Equal IR2,0]
    Passed->stPlayCnt11
    Failed->stPlayCnt00

:stPlayCnt00
    [ SetDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_cntxx ]   //有一位女士
    [ PlayFile SR1 ]
    MessageGeted(3)->stStopVoxBreak
    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stPlayCnt00
    Failed->stConnectDev

:stPlayCnt11
    [ SetDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_cntxy ]   //有一位男士
    [ PlayFile SR1 ]
    MessageGeted(3)->stStopVoxBreak
    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stPlayCnt11
    Failed->stConnectDev

:stStopVoxBreak
    [ StopVoxAction ]
    StopAction->stSetDBDeCnt 

:stConnectDev                //连接对方Device
    [ Assign IR11,0 ]//IR11记录本次时长
    [ ConnectDevice $Device1,IR4,2 ]
    Passed->stCheckOther    //立即检查对方是否在线
    Failed->stConnectVoxForFail
    MessageGeted(3)->stStopVoxBreak
:stConnectVoxForFail             //重连语音设备
    [ UnlistenDevice $Device1 ]
    [ ConnectDevice $VoxDevice,$Device1,2 ]
    Passed->stCheckFriend
    Failed->stCheckFriend
:stCheckOther          //检查对方是否仍在线上
    [ ASSIGN SR1,"select STATUS from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where DEVICEID=" ]
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]
    [ EXECSQL SR1 ]
    Passed ->stCheckOtherCount
    Failed ->stSetDBDeCnt
    TimeOut ->stSetDBDeCnt
:stCheckOtherCount
    [ Great $DBRecCount ,0 ]
    Passed->stCheckContainSelf
    Failed->stSetDBDeCnt

:stCheckContainSelf            //检查对方是否包含自己
    [ ItoS $Device1 ]
    [ Equal FD0,SR0 ]
    Passed->stCheckMRecord     //已包含
    Failed->stCheckOtherStatus
:stCheckOtherCountForStatus
    [ Great $DBRecCount ,0 ]
    Passed->stCheckOtherStatus 
    Failed->stSetDBDeCnt 
:stCheckOtherStatus
    [ Equal FD0,"0" ]
    Passed->stDelOtherSide
    Failed->stSetDBDeCnt
:stDelOtherSide       //如果对方状态为0，则删除对方
    [ ASSIGN SR1,"delete  "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where DEVICEID= " ]
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]
    [ EXECSQL SR1 ]
    Passed ->stSetDBDeCnt 
    Failed ->stSetDBDeCnt 
    TimeOut ->stSetDBDeCnt 

//录音---------start
:stCheckMRecord
    [ Equal IR25,0 ]    //是否已经录音
   Passed->stCheckSexForMRecord  //未开始录音
//   Passed->stSetTimer          //不录音
    Failed->stSetTimer			//已开始录音

:stCheckSexForMRecord    //根据性别判断是否需要录音
    [ Equal IR2,0 ]
    Passed ->stSetTimer  //女，不录音
    Failed ->stStartRecord   //男,开始录音

:stStartRecord
    [ ASSIGN SR1 , @vox_RecordPath ] 
    [ NowTime ]
    [ TimePart IR0,4 ]
    [ ItoS IR0 ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,"\"]
    [ STRCAT SR1 , $CallerID ]
    [ STRCAT SR1 , "_" ]
//    [ ItoS $Device1 ]
//    [ Strcat SR1 , SR0 ]
//    [ STRCAT SR1 , "_" ]
    [ NowTime ]
    [ STRCAT SR1,SR0 ]
    [ STRCAT SR1 , "_" ]
    [ Strcat SR1,SR11 ]
    [ STRCAT SR1,".vox" ]
    [ SetMRecord SR1 ]
    Passed->stSetTimerForRecord
    Failed->stSetTimer
      
:stSetTimerForRecord
    [ Assign IR25,1 ]   //设置录音标志，已经开始录音

    [ TIMER @MaxRecordTime ]
    TimeOut->stStopMRecord
    MessageGeted(3)->stSetDBDeCnt

:stStopMRecord
    [ Add IR11,@MaxRecordTime ]
    [ StopMRecord ]   //停止双向录音
    ->stSetTimer
//录音------end

:stSetTimer
    [ ClearDTMF ]
    [SetDTMF "#" ]
    [ Digit 20 ]
    TimeOut->stCheckOther
    MessageGeted(3)->stSetDBDeCnt
    StopDigits('#')->stGetNextByDigit//# stop chat , get next one
    StopDigits->stCheckOther

//20130502
:stGetNextByDigit
    [ PutMessage IR4,3,"" ]
    ->stSetDBDeCnt

:stSetDBDeCnt    //更新数据库状态--断开连接

    [ StopMRecord ]   //停止双向录音
    [ Assign IR25,0 ] 

    [ ASSIGN SR1,"update "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," set STATUS=" ]
    [ STRCAT SR1,"0 where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stConnectVox
    Failed ->stConnectVox
    TimeOut ->stConnectVox

:stConnectVox               //重连语音设备
    [ UnlistenDevice $Device1 ]
    [ ConnectDevice $VoxDevice,$Device1,2 ]
    Passed->stJudgeTodayCon
    Failed->stJudgeTodayCon

:stJudgeTodayCon
    [ CallFunc &judgeTodayConCount ]
    Returned(0)->stBeforeExit
    Returned->stPlayDeCnt

:stPlayDeCnt
    [ setDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath] 
    [ STRCAT SR1,@vox_break]    //对方已挂机
    [ PlayFile SR1]
    StopAction->stPlayDeCnt
    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    StopPlayBack->stCheckFriend
    DialTerminate->stCheckFriend

    Failed->stCheckFriend

:stBeforeExit
    [ CallFunc &destoryflowa ]
    Returned->stExit

:stExit
    [ RETURN 0]
}

destoryflow
{
:stWait
	Connect->stSearchConnect

:stSearchConnect
    [ UnlistenDevice $Device1 ]
    [ Assign SR1,"select STATUS from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where DEVICEID=" ]
    [ ItoS IR4 ]
    [ Strcat SR1,SR0 ]
    [ EXECSQL SR1 ]
    Passed ->stSearchConnect1
    Failed ->stSearchConnect2
    TimeOut ->stSearchConnect2

:stSearchConnect1
    [ Great $DBRecCount,0 ]
    Passed->stJudgeSelf
    Failed->stSearchConnect2

:stJudgeSelf
    [ ItoS $Device1 ]
    [ Equal FD0,SR0 ]
    Passed->stPutMessage
    Failed->stSearchConnect2

:stPutMessage
    [ PutMessage IR4,3,"" ]
    ->stSearchConnect2

:stSearchConnect2
    [ ASSIGN SR1,"select CONNECTCOUNT from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stClearDB
    Failed ->stClearDB2
    TimeOut ->stClearDB2
//    CallTerm->stClearDB
//    MessageGeted->stClearDB

:stClearDB
      [ Great $DBRecCount,0 ]
      Passed->stClearDBa
      Failed->stClearDB2

:stClearDBa
      [ Assign SR25,FD0 ]
      ->stDeleteSelf

:stClearDB2
      [ Assign SR25,"0" ]
      ->stDeleteSelf

:stDeleteSelf    
    [ ASSIGN SR1,"delete  "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeNv
    Failed ->stJudgeNv
    TimeOut ->stJudgeNv

:stJudgeNv
    [ Equal IR2,0 ]
    Passed->stExit
    Failed->stInsertJHUSER

:stInsertJHUSER
      [ CallFunc &JHUSEROUT ]
      Returned->stExit



:stExit
	[ return 0 ]

}
destoryflowa
{
:stWait
    [ UnlistenDevice $Device1 ]
    [ PutMessage IR4,3,"" ]

    [ ASSIGN SR1,"select CONNECTCOUNT from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stClearDB
    Failed ->stClearDB2
    TimeOut ->stClearDB2

:stClearDB
      [ Great $DBRecCount,0 ]
      Passed->stClearDBa
      Failed->stClearDB2

:stClearDBa
      [ Assign SR25,FD0 ]
      ->stDeleteSelf

:stClearDB2
      [ Assign SR25,"0" ]
      ->stDeleteSelf

:stDeleteSelf    
    [ ASSIGN SR1,"delete  "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeNv
    Failed ->stJudgeNv
    TimeOut ->stJudgeNv

:stJudgeNv
    [ Equal IR2,0 ]
    Passed->stExit
    Failed->stInsertJHUSER

:stInsertJHUSER
   [ CallFunc &JHUSEROUT ]
      Returned->stExit

:stExit
	[ return 0 ]

}

CheckLaKetel
{
:stWait
   [ Assign SR1,"select CALLERID from " ]
   [ Strcat SR1,@LakeTable ]
   [ Strcat SR1," where CALLERID='" ]
   [ Strcat SR1,$CallerID ]
   [ Strcat SR1,"'" ]
   [ ExecSQL SR1 ]
   Passed ->stGetcount
   Failed ->stFormatCaller
   TimeOut ->stFormatCaller
   CallTerm->stExit

:stGetcount
   [ Equal $DBRecCount,0 ]
   Passed->stFormatCaller
   Failed->stLakede
   //Passed->stContinue
   //Failed->stFormatCaller

:stFormatCaller
    [ StrRight $CallerID,11 ]
    [ StrSub SR0,1,1 ]
    [ Equal SR0,"1" ]
    Passed->stMobile
    Failed->stGHTel

:stGHTel
    [ StrRight $CallerID,11 ]
    [ Assign SR21,SR0 ]
    ->stJudgeVipBoda00

:stMobile
    [ Assign SR21,"029" ]
    [ StrRight $CallerID,11 ]
    [ Strcat SR21,SR0 ]
    ->stJudgeVipBoda00

:stJudgeVipBoda00
   [ Assign SR1,"select VIPUID from T_vipuid where VIPUID='" ]
   [ Strcat SR1,SR21 ]
   [ Strcat SR1,"'" ]
   [ ExecSQL SR1 ]
   Passed ->stGetCount2
   Failed ->stContinue
   TimeOut ->stContinue
   CallTerm->stExit

:stGetCount2
   [ Equal $DBRecCount,0 ]
   Passed->stContinue
   Failed->stLakede

:stLakede
   [ Assign IR15,0 ]
   ->stExit

:stContinue
   [ Assign IR15,1 ]
   ->stExit

:stExit
   [ Return IR15 ]

}

GetMusicPath
{
:stWait
/*    [ Assign SR1,"e:\music\mu" ]
    [ IntRand 22 ]
    [ ItoS IR0 ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,".vox"]
    ->stExit
*/

     [ Assign SR1,"select CODE from T_11SONG where NUM='" ]
     [ intRand 45 ]
     [ Inc IR0 ]
     [ ItoS IR0 ]
     [ Strcat SR1,SR0 ]
     [ Strcat SR1,"'" ]
     [ ExecSQL SR1 ]
     Passed->stJudgeDBCount
     Failed->stDefaltMus
     TimeOut->stDefaltMus

:stJudgeDBCount
     [ Great $DBRecCount,0 ]
     Passed->stGetMusPath
     Failed->stDefaltMus

:stGetMusPath
    [ Assign SR1,"select SONGPATH from T_MUSIC where SONGCODE='" ]
    [ Strcat SR1,FD0 ]
    [ Strcat SR1,"'" ]
     [ ExecSQL SR1 ]
     Passed->stAssign
     Failed->stDefaltMus
     TimeOut->stDefaltMus

:stAssign
    [ Assign SR1,FD0 ]
     ->stExit

:stDefaltMus
     [ Assign SR1,"H:\MUSIC\00398.vox" ]
     ->stExit
:stExit
   [ Return 0 ]
}

judgeTodayConCount
{
:stWait
     [ Equal SR2,"0" ] //女士则直接跳出不判断
     Passed->streturn1
     Failed->stJudgeVip

:stJudgeVip
      [ Equal SR17,"1" ]
      Passed->streturn1    //vip user 直接跳出不判断
      Failed->stGetUserTel


:stGetUserTel
    [ StrRight $CallerID,11 ]
    [ StrSub SR0,1,1 ]
    [ Equal SR0,"1" ]
    Passed->stMobile
    Failed->stGHTel

:stGHTel
    [ StrRight $CallerID,11 ]
    [ Assign SR21,SR0 ]
    ->stjudgeIfOldVip

:stMobile
    [ Assign SR21,"029" ]
    [ StrRight $CallerID,11 ]
    [ Strcat SR21,SR0 ]
    ->stjudgeIfOldVip

:stjudgeIfOldVip
  [ Assign SR1,"select * from T_vipuid where VIPUID='" ]
   [ Strcat SR1,SR21 ]
   [ Strcat SR1,"' and (convert(datetime,STARTDATE)>getdate() or  convert(datetime,STOPDATE+' 23:59:59')<=getdate())" ]
   [ ExecSQL SR1 ]
    Passed->stCheckIfOldvip
    Failed->stSearch
    Timeout->stSearch

:stCheckIfOldvip
    [ Great $DBRecCount,0 ]
    Passed->stDelSelf
    Failed->stSearch

:stSearch
     [ Assign SR1,"select    SUM(convert(int,a.PARAM2))  xasum from jh.dbo.xa_account a  where  a.SYSTEMID<>'READED'" ]
	[ StrCat SR1," and a.ACCOUNTID='" ]
     [ Strcat SR1,$CallerID ]
     [ Strcat SR1,"' AND CHARINDEX(SERVICEID,'"]
     [ Strcat SR1,@serviceList ]
     [ Strcat SR1,"')>0" ]
     [ ExecSql SR1 ]
     Passed->stgetaccountRec
     Failed->setaccount0
     Timeout->setaccount0
:stgetaccountRec
      [ Great $DBRecCount,0 ]
      Passed->stsetaccountSum
      Failed->setaccount0

:stsetaccountSum
	[ StoI FD0 ]
      [ Assign IR28,IR0 ]
      ->stSearchbill

:setaccount0
	[ Assign IR28,0 ]
      ->stSearchbill

:stSearchbill
	[ Assign SR1,"select    SUM(convert(int,a.PARAM2))  xasum from jh.dbo.xa_bill_year a  where   CALLDATE='" ]
	[ NowTime ]
     [ Strsub SR0,1,8 ]
	[ StrCat SR1,SR0 ]
	[ StrCat SR1,"' and a.ACCOUNTID='" ]
     [ Strcat SR1,$CallerID ]
     [ Strcat SR1,"' AND CHARINDEX(SERVICEID,'"]
     [ Strcat SR1,@serviceList ]
     [ Strcat SR1,"')>0" ]
     [ ExecSql SR1 ]
     Passed->stgetbillRec
     Failed->stJudgeNow
     Timeout->stJudgeNow
:stgetbillRec
      [ Great $DBRecCount,0 ]
      Passed->staddbillSum
      Failed->stJudgeNow

:staddbillSum
	[ StoI FD0 ]
      [ add IR28,IR0 ]
      ->stJudgeNow

:stJudgeNow
      [ Add IR28,IR26 ]
	[ Less IR28,@noVipPerDayCon ]
      Passed->stSearchNowMoth
	Failed->stDelSelf

:stSearchNowMoth    //查找当月除了今天的次数
	[ Assign SR1,"select    SUM(convert(int,a.PARAM2))  xasum from jh.dbo.xa_bill_year a  where   CALLDATE<'" ]
	[ NowTime ]
     [ Strsub SR0,1,8 ]
	[ StrCat SR1,SR0 ]
	//[ StrCat SR1,"' and datediff(day,CALLDATE,getdate())<120 and a.ACCOUNTID='" ]
     [ StrCat SR1,"' and datediff(day,CALLDATE,getdate())<" ]
     [ Strcat SR1,@noVipNDay ]
     [ Strcat SR1," and a.ACCOUNTID='" ]

     [ Strcat SR1,$CallerID ]
     [ Strcat SR1,"' AND CHARINDEX(SERVICEID,'"]
     [ Strcat SR1,@serviceList ]
     [ Strcat SR1,"')>0" ]
     [ ExecSql SR1 ]
     Passed->stgetNowMonthbillRec
     Failed->stJudgeNowMonth
     Timeout->stJudgeNowMonth

:stgetNowMonthbillRec
      [ Great $DBRecCount,0 ]
      Passed->staddNowMothbillSum
      Failed->stJudgeNowMonth

:staddNowMothbillSum
	[ StoI FD0 ]
      [ add IR28,IR0 ]
      ->stJudgeNowMonth

:stJudgeNowMonth
	[ Less IR28,@noVipNDayCon ]
      Passed->streturn1
	Failed->stDelSelf


:stDelSelf
     [ Assign SR1,"delete from "]
	[ Strcat SR1,@OnlineUserTable ]
	[ Strcat SR1," where USERCODE='" ]
     [ Strcat SR1,$CallerID ]
     [ Strcat SR1,"'" ]
     [ ExecSql SR1 ]
     Passed->stCheckCon
     Failed->stCheckCon
     Timeout->stCheckCon

:stCheckCon
    [ Great IR26,0 ]
    Passed->stPlaynovipHaveCon
    Failed->stPlaynovip


:stPlaynovipHaveCon
	[ Assign $BillParam3,"novip_" ]
      [ ItoS IR28 ]
      [ Strcat $BillParam3,SR0 ]
      [ ClearDTMF ]
	[ SetDTMF "0" ]
      [ PlayFile ".\vox\noviphavecon.vox" ]
      StopPlayBack ->stGetOpeDigit
      DialTerminate ->stGetOpeDigit
      Failed ->stGetOpeDigit

:stPlaynovip
	[ Assign $BillParam3,"novip_" ]
      [ ItoS IR28 ]
      [ Strcat $BillParam3,SR0 ]
      [ ClearDTMF ]
	[ SetDTMF "0" ]
      [ PlayFile ".\vox\novip.vox" ]
      StopPlayBack ->stGetOpeDigit
      DialTerminate ->stGetOpeDigit
      Failed ->stGetOpeDigit

:stGetOpeDigit
    [ Digit 5 ]
    StopDigits('0')->stSingerTo0180
    StopDigits->stPlaynovip2
    TimeOut->stPlaynovip2

:stPlaynovip2
      [ ClearDTMF ]
	[ SetDTMF "0" ]
      [ PlayFile ".\vox\novip.vox" ]
      StopPlayBack ->stGetOpeDigit2
      DialTerminate ->stGetOpeDigit2
      Failed ->stGetOpeDigit2

:stGetOpeDigit2
    [ Digit 5 ]
    StopDigits('0')->stSingerTo0180
    StopDigits->streturn0
    TimeOut->streturn0



:stSingerTo0180
   [ SINGLESTEPTRANSFER "966890180",$CallerID,$BillParam1 ]
   Passed ->streturn0
   Failed ->streturn0


:streturn1
      [ Assign IR29,1 ]
	->stExit


:streturn0
      [ Assign IR29,0 ]
	->stExit


:stExit
	[ return IR29 ]
}