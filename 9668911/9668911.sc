//һ�����ʶ(T_JH9668911)
//create table T_JH9668911(
//    DEVICEID                        int               not null ,    --0,�û��豸
//    SEX                             int               null     ,    --1,�Ա�
//    STATUS                          int               null     ,    --2,�豸״̬
//    USERCODE                        char(20)          null     ,    --3,�û�����
//    LOGINDT                         char(20)          null     ,    --4,��¼ʱ��
//    CONNECTDT                       char(20)          null     ,    --5,����ʱ��
//    CONNECTCOUNT                    int               null     ,    --6,���Ӵ���
//    CONSTRAINT PKDEVICEID Primary Key NONCLUSTERED (DEVICEID)     
//)

//�����͵ı�
//create table T_LakeTel(
//    CALLERID                        char(20)          null     ,    --1,��ź���
//    STATUS                         char(2)          null           --2,��ű�ʶ(������)
//)


//00����Ա��
//create table T_9668911_bak(
//TEL char(20),
//OPERATOR char(3),
//STATUS char(1),
//PARAM varchar(255)
//)


//���ӳ�ʼ��������
#iniparam int  @maxoplen
#iniparam int  @MusicCount
#iniparam int  @MaxRecordTime
#iniparam int  @MaxBillMonth
#iniparam int  @MaxBillMonthO
#iniparam int  @hotadd
#iniparam string @vox_outOperatorTel
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
#iniparam string @Work160Code
#iniparam string @LakeTable
#iniparam string @Vox_cantrans
#iniparam string @vox_dy00b
#iniparam string @vox_dy11b
#iniparam string @vox_dyLR
#iniparam string @9668911bak
#iniparam string @nvsearchLim
#iniparam string @callout
#iniparam string @serviceList
#iniparam string @noVipNDay
#iniparam int @warntime2
#iniparam int @warntime3
#iniparam int @numof9668911bak
#iniparam int @maxsumtime
#iniparam int @ltmaxsum
#iniparam int @ydmaxsum
#iniparam int @dxmaxsum
#iniparam int @VIPYouxianCon
#iniparam int @noVipPerDayCon
#iniparam int @noVipNDayCon

#include "H:\subfunc\rount.sub"

//���Ӻ�������
#function destoryflow
#function destoryflowa
#function CheckLaKetel
#function GotoAnother
#function GetMusicPath
#function JudgeCaller
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
    [ Assign SR1,"delete T_JH9668911 where DEVICEID=" ]
    [ ItoS $Device1 ]
    [ Strcat SR1,SR0 ]
    [ ExecSQL SR1 ]
    Passed->stBillBegin
    Failed->stBillBegin
    TimeOut->stBillBegin

:stBillBegin
    [ NowTime ]
    [ Assign IR28,IR0 ]// IR28 save the start time of this thread, **don't update it
    [ Assign IR4,0 ]	//IR4 save the user status, 0-free, others-busy
    [ OnCallCleared  &destoryflow ]

    [ SetDBTimeOut 30 ]                 //dawei add 2003.11.12 22:20
    [ BillBegin 1,0,$ServiceItemNo ]
    Verify(0)  -> stPlayZFCX
    Verify(-1) -> stExit

:stPlayZFCX

////////////////for118
    [ Assign SR10,$InputParam ]
    [ ParseParam SR10,1,"$" ]
    [ Equal SR0,"callout" ]
//    [ Equal $CallerID,"1005" ]
    Passed->stGetDigitForMessage
    Failed->stguanggao

//��沥��*****************************************
:stguanggao
    [ Equal $CalledID,"96689000" ]      //��沥��
    //Passed->stBillEnd96689003
    Passed->stPlayZFCX2

    Failed->stPlayZFCX2

:stBillEnd96689003
     [ ClearDTMF ]
     [ SetDTMF "" ]
     [ ASSIGN IR2,0 ]
     [ ASSIGN SR1,".\vox\vox\" ]
     [ STRCAT SR1,"songge.vox" ]               //���������96689555���̨Ϊ���ĸ�������������....
     [ PLAYFILE SR1 ]
     StopPlayBack ->stGetGEQU
     DialTerminate ->stGetGEQU
     Failed ->stGetGEQU

:stGetGEQU
    [ ClearDTMF ]
    [ SetDTMF "?" ]
    [ IntRand 20 ]
    [ itos IR0 ]
    [ ASSIGN SR2,SR0 ]
    [ ASSIGN SR1,".\vox\gequ\" ]
    [ STRCAT SR1,SR2 ]
    [ STRCAT SR1,".vox" ]               
    [ PLAYFILE SR1 ]
    StopDigits->stxuanchuan
    DialTerminate ->stxuanchuan
    StopPlayBack ->stxuanchuan
    TimeOut->stxuanchuan
    Failed ->stxuanchuan

:stxuanchuan
    [ ClearDTMF ]
    [ SetDTMF "" ]
    [ ASSIGN SR1,".\vox\vox\" ]
    [ STRCAT SR1,"xuanchuan.vox" ]               //����Ϊ�㻹�ṩ��ȫʡ���ļ��齻��ƽ̨......
    [ PLAYFILE SR1 ]
    StopPlayBack ->stGetGEQU
    DialTerminate ->stGetGEQU
    Failed ->stGetGEQU


//�������ת��***************************************************************************

:stPlayZFCX2
    [ StrSub $CalledID,6,6 ]      //ʵ��ʹ��
    [ Assign SR2,SR0 ]
    [ Equal SR2,"0" ]   //�����ԡ�966890X����ʼ�ĺ��붼�ж�ΪŮʿ,�Ʒ�ʱ���
    Passed->stJudge008ForGuanggao//0XŮʿ
    Failed->stJudge966899//�ж��Ƿ�9XX

:stJudge008ForGuanggao
    [ Equal $CalledID,"96689008" ]
    Passed->stJudge966899
    Failed->stPlayMianFei

:stJudge966899              //�ƶ�966899XXX�ʷ�Ϊ��0.09Ԫ�����Բ�����úŶΡ���ʾ�û����ĺ���
    [ Equal SR2,"9" ]
    Passed->stPlayChangeCalled
    Failed->stJudgeYDCh//�ж��Ƿ�22��ԭ��������Ůʿ����

:stJudgeYDCh
    [ StrStr "$9668922$9668924$",$CalledID ]      //�ж�Ůʿ�Ƿ���22�����򲥷�"�������ѱ��Ϊ9668900������"��������ʿ
    [ Great IR0,0 ]
    Passed->stPlayChangeCalled
    Failed->stPlayFeelv//11��ʿ

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
    [ ParseParam $InputParam,1,"$" ]
    [ Equal SR0,"callout" ]
    Passed->stParaminput
    Failed->stplaymianfei2

:stplaymianfei2
    [ PlayFile "h:\vox\mianfei.VOX" ]
    StopPlayBack->stParaminput
    DialTerminate->stParaminput
    Failed->stParaminput

:stAlert    //20080716add������ֹ�޷�����H��
    [ Assign SR1,"insert into T_PREDIAL values('3c2djtfm7q4',0,'02988239668'" ]
    [ Strcat SR1,",'02983008068','9668951899',0,'2008-07-16 17:59:53','2008-07-16 17:59:53'" ]
    [ Strcat SR1,",20,'1^h:\Record\temp\02988239668_3c2djtfm7q4.tmp^d:\gzesun\����Ӣ-������.vox^20080716175953^'" ]
    [ Strcat SR1,",'168',1,0,31,'96689000')"]
    [ ExecSQL SR1 ]
    Passed->stExit
    Failed->stExit
    TimeOut->stExit

:stPlayFeelv
      [ StrSub $InputParam,1,8 ]
      [ Equal SR0,"96689003" ]    //vip user not playfee 20150303
      Passed->stJudgeForLR
      Failed->stJudgeVipBoda11



//20150112�ж��Ƿ�VIPֱ�Ӳ���11
:stJudgeVipBoda11
   [ Assign SR1,"select * from T_vipuid where VIPUID='" ]
   [ Strcat SR1,$CallerID ]
   [ Strcat SR1,"' and convert(datetime,STARTDATE)<=getdate() and convert(datetime,STOPDATE+' 23:59:59')>getdate()" ]
   //[ Strcat SR1,"'" ]
   [ ExecSQL SR1 ]
    Passed->stCheckvip
    Failed->stJudgeNoVipTodayCon
    Timeout->stJudgeNoVipTodayCon

:stCheckvip
    [ Great $DBRecCount,0 ]
    Passed->stPlayVipbo003
    Failed->stJudgeNoVipTodayCon

:stPlayVipbo003
    [ ClearDTMF ]
    [ PlayFile ".\vox\vipbo003.vox" ]
     StopPlayBack->stExit
    DialTerminate->stExit
    Failed->stExit

:stJudgeNoVipTodayCon
    [ CallFunc &judgeTodayConCount ]
    Returned(0)->stBeforeExit
    Returned->stPlayFeelv2


:stPlayFeelv2
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
    [ PlayFile ".\vox\lrjydhw.VOX" ] //�ж��Ƿ���Ҫѡ����Ů
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
    [ Assign SR9,SR0 ]
    ->stParaminput

:stBeforeInitFemale
    [ Assign SR9,SR0 ]
    //[ Assign $BillParam3,$CalledID ]
    ->stParaminput

:stParaminput
    [ ParseParam $InputParam,1,"$" ]//ZHANGPENGGAI081023
    [ ASSIGN SR14,SR0 ]
    [ ParseParam $InputParam,1,"$" ]
    [ ASSIGN SR23,SR0 ]
    [ Equal $InputParam,"" ]
    Passed->stParaminput2
    Failed->stParaminput1

:stParaminput1
    [ ASSIGN $BillParam1,$InputParam ]   //һ��ͨ
    ->stGetSex

:stParaminput2
    [ ItoS $Device1 ]
    [ Assign SR19,SR0 ]           
    [ NowTime ]
    [ Strcat SR19,SR0 ]
    [ ASSIGN $BillParam1,SR19 ]   //һ��ͨ
    ->stGetSex

:stGetSex
    [ Assign IR25,0 ]    //�Ƿ���˫��¼����0��δ
    [ Assign IR26,0 ]   //��¼����
    [ Equal $CallerID,"02913991974000" ]
    Passed->setless
    Failed->stVipUserYouxian



//20150109���ӣ�vip�û�Ĭ�����Ƚ�ͨ1��
:stVipUserYouxian
      [ StrSub $BillParam1,1,8 ]
      [ Equal SR0,"96689003" ]//vip user
      Passed->setless
      Failed->stContineGetSex

:setless
    [ Assign IR26,@VIPYouxianCon ]
    ->stContineGetSex

:stContineGetSex
    [ Assign IR27, @MaxRecordTime ]
    [ IDiv  IR27,10 ]//¼������,ÿ��10��
    [ Assign IR21,0 ]   //��������
    [ StrSub $CalledID,6,6 ]      //ʵ��ʹ��
    [ ASSIGN SR2,SR0 ] 
    [ Equal SR2,"0" ]
    Passed->stJudgeFor008
    Failed->stJudgetelyd

:stJudgeFor008
    [ Equal $CalledID,"96689008" ]
    Passed->stJudgetelyd
    Failed->stInitFemale

:stJudgetelyd
    [ StrStr "$96689169$96689189$9668982",$CalledID ]
    [ Great IR0,0 ]
    Passed->stJudgeMF
    Failed->stJudgetelydbb

:stJudgeMF
    [ Equal SR9,"2" ]
    Passed->stInitFemale
    Failed->stJudgetelydbb


:stJudgetelydbb
     [ CallFunc &JudgeGMIC ]           //�ж��Ƿ�Ϊ����绰��IC���绰
     Returned(1)->stExit
     Returned->stInitMale


:stInitFemale
    [ ASSIGN IR2,0 ]           //Ů
    [ ASSIGN IR3,1 ]           //����
    -> stChecktel
:stInitMale
    [ ASSIGN IR2,1 ]           //��
    [ ASSIGN IR3,0 ]           //��Ů
    -> stJudgeCallerLimit

:stJudgeCallerLimit    //shanxihd panduan
    [ CallFunc &JudgeCaller ]
     returned(1)->stClearDB
     returned->stExit

//����Ƿ�Ϊ���Ϳ��еĵ绰
:stChecktel
   [ CallFunc &CheckLaKetel ]
   Returned(0)->stExit
	//Returned(0)->stClearDB
   Returned->stClearDB

:stClearDB                      //ɾ����ͬdevice�ļ�¼
    [ ASSIGN SR1,"delete  T_JH9668911 where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeForOp9668900
    Failed ->stJudgeForOp9668900
    TimeOut ->stJudgeForOp9668900

//////////////////////////////////////////����Ƿ�Ϊϵͳ�������Ա��������
:stJudgeForOp9668900
    [ Assign SR10,$InputParam ]
    [ ParseParam SR10,1,"$" ]
    [ Equal SR0,"callout" ]
//    [ Equal $CallerID,"1005" ]
    Passed->stGetDigitForMessage
    Failed->stAddToDB

:stGetDigitForMessage
    [ Assign $BillParam2,SR10 ]
    [ ClearDTMF ]
   // [ SetDTMF "#" ]
    //[ Digit 10 ]
   // StopDigits('#')->stAddToDBForOpbb
    //StopDigits->stAddToDBForOpbb
    //TimeOut->stAddToDBForOpbb
    ->stAddToDBForOpbb



/*
:stAddToDBForOp
    [ Assign SR1,"update t_outbak set PARAM1='suc' where TEL='" ]
    [ Strcat SR1,$InputParam ]
    [ Strcat SR1,"'" ]
    [ ExecSQL SR1 ]
    Passed->stAddToDBForOpbb
    Failed->stAddToDBForOpbb
    TimeOut->stAddToDBForOpbb
*/
:stAddToDBForOpbb
    [ ParseParam $InputParam,2,"$" ]
    [ StoI SR0 ]
    [ Assign IR4,IR0 ]

/////////////////////////////20080526add
    [ Assign SR1,"select STATUS from T_JH9668911 where DEVICEID=" ]
    [ ItoS IR4 ]
    [ Strcat SR1,SR0 ]
    [ ExecSQL SR1 ]
    Passed->stOpContinue
    Failed->stExit
    TimeOut->stExit

:stCheckStatusForOp
    [ Equal FD0,"9999" ]
    Passed->stOpContinue
    Failed->stExit

:stOpContinue
/////////////////////////////////
    [ ASSIGN SR1,"insert into  T_JH9668911 values(" ]
    [ ItoS $Device1 ]            //device
    [ STRCAT SR1,SR0 ]
    [ STRCAT SR1,"," ]
    [ ItoS IR2 ]
    [ STRCAT SR1,SR0 ]           //sex
    [ STRCAT SR1,"," ]
    [ ItoS IR4 ]
    [ Strcat SR1,SR0 ]          //status
    [ Strcat SR1,",'" ]
    [ Strcat SR1,$CallerID ]      //USERCODE
    [ Strcat SR1,"','" ]
    [ NowTime ]
    [ Strcat SR1,SR0 ]             //LOGINDT
    [ Strcat SR1,"',''," ]          //CONNECTDT
    [ ItoS IR26 ]
    [ Strcat SR1,SR0 ]             //CONNECTCOUNT
    [ Strcat SR1,")" ]
    [ EXECSQL SR1 ]
    Passed ->stSetOperator
    Failed ->stExit
    TimeOut ->stExit

:stSetOperator
    [ Assign SR1,"select OPERATOR from T_9668911_bak where TEL='" ]
    [ ParseParam $InputParam,3,"$" ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stSetOperator2
    Failed ->stPutMessage
    TimeOut ->stPutMessage

:stSetOperator2
    [ Assign $Operator,FD0 ]
    ->stPutMessage


////////////////////////////////////////////////////////////////////////

:stAddToDB                           //���뱾device
    [ ASSIGN SR1,"insert into  T_JH9668911 values(" ]
    [ ItoS $Device1 ]
    [ STRCAT SR1,SR0 ]            //device
    [ STRCAT SR1,"," ]
    [ ItoS IR2 ]
    [ STRCAT SR1,SR0 ]           //sex


    [ StrSub $CallerID,1,6 ]
    [ Equal "029118",SR0 ]
    Passed->st118notconn1
    Failed->stconncontinue
:st118notconn1
    [ STRCAT SR1,",1,'"]          //118 status 1
    ->stconncontinue2
:stconncontinue
    [ STRCAT SR1,",0,'" ]          //status
    ->stconncontinue2

:stconncontinue2
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
    [ STRCAT SR1,@vox_failInsDB ]    //$������ݿ��¼ʧ��
    [ PlayFile SR1]
    StopPlayBack->stExit
    DialTerminate->stExit
    Failed->stExit

:stPlayWarning
    [ ClearDTMF ]
    ->stCheckFriend
    //[ PlayFile ".\vox\warning.vox" ]//�����̨���֡�����
    //StopPlayBack->stCheckFriend
    //DialTerminate->stCheckFriend
    //Failed->stCheckFriend
    //StopAction->stCheckFriend

    //MessageGeted(0)->stStopVox
   // MessageGeted(1)->stStopVox
    //MessageGeted(3)->stSetDBDeCnt


:stCheckFriend               //����Ƿ����������ѣ��Է��һ���Ҳ���ص����
    [ Equal IR2,0 ]
    Passed->stCheckFriendf  //����Ů����������ʱ������С��ͨ������
    Failed->stCheckFriendm
/////////////////////////////////
:stCheckFriendf
    [ StrLen $Operator ]
    [ Great IR0,0 ]
    Passed->stOperatorNotsearchwyf
    Failed->stConinuteSearchNanshiUser

:stOperatorNotsearchwyf
    [ ASSIGN SR1,"select DEVICEID,USERCODE from T_JH9668911 where STATUS=0 and SEX=1 and USERCODE<>'02913991974000' and USERCODE not in (select TEL from T_JHUSER where SUMCOU>" ]
    [ Strcat SR1,@nvsearchLim ]
    [Strcat SR1,")" ]
    [ Strcat SR1," order by CONNECTCOUNT,LOGINDT" ]
    [ EXECSQL SR1 ]
    Passed ->stCheckFriendf1
    Failed ->stCheckFriendm
    TimeOut ->stWelcome


:stConinuteSearchNanshiUser
    [ ASSIGN SR1,"select DEVICEID,USERCODE from T_JH9668911 where STATUS=0 and SEX=1 and USERCODE not in (select TEL from T_JHUSER where SUMCOU>" ]
    [ Strcat SR1,@nvsearchLim ]
    [Strcat SR1,")" ]
    //[ Strcat SR1," and  (charindex(substring(USERCODE,4,3),'" ]
    //[ Strcat SR1,"134,135,136,137,138,139,150,151,152,158,159,182,183,187,188,133,153,189,029,091"]
    //[Strcat SR1,"')>0 OR charindex(substring(USERCODE,1,4),'0298,0293')>0)" ]
    [ Strcat SR1," order by CONNECTCOUNT,LOGINDT" ]
    [ EXECSQL SR1 ]
    Passed ->stCheckFriendf1
    Failed ->stCheckFriendm
    TimeOut ->stWelcome

:stCheckFriendf1
    [ Great $DBRecCount,0 ]
    Passed->stSetDBCntOKForFind
    Failed->stCheckFriendm



///////////////////////


:stCheckFriendm
    [ Assign IR4,0 ]

    [ ASSIGN SR1,"select DEVICEID,USERCODE from T_JH9668911 where SEX=" ]
    [ ItoS IR3 ]
    [ STRCAT SR1,SR0 ]
    [ STRCAT SR1," and STATUS=0  and USERCODE NOT IN (SELECT CALLERID FROM T_LakeTel) " ]
    [ Strcat SR1," order by  CONNECTCOUNT,LOGINDT" ]

    [ EXECSQL SR1 ]
    Passed ->stCheckCount
    Failed ->stJudgeForNewUser
    TimeOut ->stWelcome

:stCheckCount
    [ Great $DBRecCount,0 ]
    Passed->stSetDBCntOKForFind
    Failed->stJudgeForNewUser



///////////////////////////////////////////wyfadd20041223for��ʿ���û���������ڲ�ŮʿС��ͨ
:stJudgeForNewUser
    [ Equal IR2,0 ]
    Passed->stWelcome
//    Failed->stJudgeNowMonthSumCou//�������Ա,dba error,stop out  20170210 stop out operator call
    Failed->stWelcome//�����������ͣ����ֹͣ�������Ա

:stJudgeNowMonthSumCou
    [ Assign SR1,"select SUMCOU from T_JHUSER where TEL ='" ]
    [ StrRight $CallerID,11 ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeTimeOnLine
    Failed ->stNewUser
    TimeOut ->stWelcome

//wyfadd 20111102
:stNewUser
    [ Assign IR19,0 ]
    ->stJudeuserType

//wyfend 20111102



:stJudgeTimeOnLine
    [ StoI FD0 ]
    [ Assign IR19,IR0 ]
    ->stJudeuserType

:stJudeuserType
    [ CallFunc &CheckTelyd ]
    Returned(1)->ststJudgeLTFrom003    //1-��ͨ,
    Returned(2)->stYDMaxSum    //2�ƶ�,
    Returned(3)->stDXMaxSum    //3-����
    Returned->stDXMaxSum


:ststJudgeLTFrom003    //20110426add  ��ͨ�û���Ա�ſɸ���ʱ��Ѱ�һ���Ա
    [ StrSub $InputParam,1,8 ]
    [ Equal SR0,"96689003" ]
    Passed->stLTMaxSum
    Failed->stWelcome

:stLTMaxSum
    [ Assign IR18,@ltmaxsum ]
    ->stJudgeCalloutSum


:stYDMaxSum
    [ Assign IR18,@ydmaxsum ]
    ->stJudgeCalloutSum
:stDXMaxSum
    [ Assign IR18,@dxmaxsum ]
    ->stJudgeCalloutSum

:stJudgeCalloutSum
    [ NowTime ]
    [ TimePart IR0,3 ]
    [ Assign IR20,IR0 ]
    [ Great IR20,20 ]//����20��
    Passed->stAddIR19ForHot
    Failed->stJudge8dianhou

:stJudge8dianhou
    [ Less IR20,2 ]//С��2��
    Passed->stAddIR19ForHot
    Failed->stBJNow

:stAddIR19ForHot
    [ Add IR19,@hotadd ]//�����û����ȵ�ʱ�䣨20��-1�㣩�ӻ���Ա��ʱ����ԭ�������ļ��������ټ���X����
    ->stBJNow

:stBJNow//�Ƚ��Ƿ�Ҫת�ӻ���Ա
    [ Add IR19,4 ]
    [ Less IR19,IR18 ]//    [ Equal $CallerID,"02988239668" ]
    Passed->stJudgeOut1
    Failed->stWelcome

//wyfadd20101209for���û����������Աһ��
:stJudgeOut1
    [ Less IR12,1 ]
    Passed->stGetOrder
    Failed->stWelcome

:stGetOrder
    [ Assign IR12,1 ]
    [ ReadFlowIniFile "Parameters","9668911bak" ]
    [ Assign SR15,SR0 ]
    ->stJudgeManYou

:stJudgeManYou//20080616add�ж��Ƿ������û�
    [ Strlen $CallerID ]
    [ Less IR0,11 ]
    Passed->stWelcome
    Failed->stJudgeOperatorNum


:stJudgeOperatorNum//���û���Ȩ���ͻ���ԱС��ͨ��ͨ
///////////////////////////////////////////////wyf20100420start
    //[ Equal $CallerID,"02988239688" ]
    //Passed->stSearchWaixianOp
    //Failed->stCallOp2222222222

//:stCallOp2222222222
    [ Assign SR1,"select TEL,SX from T_9668911_bak where STATUS='1' and PARAM='0' and datalength(TEL)=4 and convert(INT,SX)>=" ]//wyf20101217modi���Ȳ������߻���Ա
    [ Strcat SR1,SR15 ]
    [ Strcat SR1," order by convert(INT,SX)" ]
    [ EXECSQL SR1 ]
    Passed ->stJudge9668911bakDBCount
    Failed ->stCallOutLessPart
    TimeOut ->stCallOutLessPart

:stJudge9668911bakDBCount
    [ Great $DBRecCount,0 ]
    Passed->stReSave
    Failed->stCallOutLessPart

:stCallOutLessPart
    [ Assign SR1,"select TEL,SX from T_9668911_bak where STATUS='1' and PARAM='0' and datalength(TEL)=4 and convert(INT,SX)<" ]
    [ Strcat SR1,SR15 ]
    [ Strcat SR1," order by convert(INT,SX)" ]
    [ EXECSQL SR1 ]
    Passed ->stJudge9668911bakDBCount2
    Failed ->stSearchWaixianOp
    TimeOut ->stWelcome

:stJudge9668911bakDBCount2
    [ Great $DBRecCount,0 ]
    Passed->stReSave
    Failed->stSearchWaixianOp


//wyfadd20101217start

:stSearchWaixianOp  //�������߻���Ա
    [ Assign SR1,"select TEL,SX from T_9668911_bak where STATUS='1' and PARAM='0' and datalength(TEL)<>4 and convert(INT,SX)>=" ]//wyf20100420modi
    [ Strcat SR1,SR15 ]
    [ Strcat SR1," order by convert(INT,SX)" ]
    [ EXECSQL SR1 ]
    Passed ->stSearchWaixianOp2
    Failed ->stSearchWaixianOp3
    TimeOut ->stSearchWaixianOp3

:stSearchWaixianOp2
    [ Great $DBRecCount,0 ]
    Passed->stReSave
    Failed->stSearchWaixianOp3

:stSearchWaixianOp3
    [ Assign SR1,"select TEL,SX from T_9668911_bak where STATUS='1' and PARAM='0' and datalength(TEL)<>4 and convert(INT,SX)<" ]
    [ Strcat SR1,SR15 ]
    [ Strcat SR1," order by convert(INT,SX)" ]
    [ EXECSQL SR1 ]
    Passed ->stSearchWaixianOp4
    Failed ->stWelcome
    TimeOut ->stWelcome

:stSearchWaixianOp4
    [ Great $DBRecCount,0 ]
    Passed->stReSave
    Failed->stWelcome

//wyfadd20101217end



/*
:stJudgeCallOutNext
    [ Inc IR12 ]
    [ Great IR12,@numof9668911bak ]
    Passed->stWelcome
    Failed->stNextOrder

:stNextOrder
    [ StoI SR15 ]
    [ Mod IR0,@numof9668911bak ]
    [ Inc IR0 ]
    [ ItoS IR0 ]
    [ Assign SR15,SR0 ]
    ->stJudgeOperatorNum
*/


//���ҳɹ�����ʼ���л���Աwyfadd20100420
:stReSave
    [ Assign SR15,FD1 ]
    [ StoI SR15 ]
    [ Inc IR0 ]
    [ Mod IR0,@numof9668911bak ]
    [ ItoS IR0 ]
    [  WriteFlowIniFile "Parameters","9668911bak",SR0 ]
    ->stCallOutOperator
/////////////////////////////////////////wyf20100420end

:stCallOutOperator
    [ Assign SR8,"callout$" ]
    [ ItoS $Device1 ]
    [ Strcat SR8,SR0 ]
    [ Strcat SR8,"$" ]
    [ Strcat SR8,FD0 ]
    [ Strcat SR8,"$" ]
    [ Assign SR7,FD0 ]


    [ Sub IR19,4 ]
    [ Sub IR18,IR19 ] //081017wyfadd
    [ ItoS IR18 ]
    [ Strcat SR8,SR0 ]
    [ Strcat SR8,"$" ]

    [ Assign SR1,"update T_9668911_bak set PARAM='1',ZH='" ]
    [ Strcat SR1,$CallerID ]
    [ Strcat SR1,"' where TEL='" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stNewUserInit
    Failed ->stWelcome
    TimeOut->stWelcome
/*
:stBakForFail
     [ Assign SR1,"insert into t_outbak values('" ]
     [ Strcat SR1,SR8 ]
     [ Strcat SR1,"','" ]
     [ NowTime ]
     [ Strcat SR1,SR0 ]
     [ Strcat SR1,"','fail')" ]
    [ EXECSQL SR1 ]
    Passed ->stNewUserInit
    Failed ->stNewUserInit
    TimeOut->stNewUserInit
*/
:stNewUserInit
    [ ScheduleCall SR7,"96689000",SR8 ]
    Passed->stSetDBCntOKForFindForNewUser
    Failed->stResetOp
    CallTerm(1)->stResetSelf
    CallTerm(2)->stResetSelf
    CallTerm(3)->stResetSelf
    CallTerm(4)->stResetSelf
    CallTerm(5)->stResetSelf

     //[ ClearDTMF ]
     //->stWelcome


/*
:stNewUserInit
//      [ ScheduleCall SR7,"96689000",SR8 ]
	[ Assign SR1,"insert into T_PREDIAL values('" ]
        [ NowTime ]
	[ ItoS IR0 ]
	[ Strcat SR1,SR0 ]
        [ Strcat SR1,"',0,'96689000','" ]
	[ Strcat SR1,SR7 ]
	[ Strcat SR1,"','96689000',0,'" ]

    [ NOWTIME ]
    [ ASSIGN  SR6,SR0 ]  //��ǰʱ��
    [ STRSUB SR6,1,4 ]
    [ ASSIGN SR9,SR0 ]
    [ STRCAT SR9,"-"]

    [ STRSUB SR6,5,6]
    [ STRCAT SR9,SR0]
    [ ASSIGN SR25,SR0]    
    [ STRCAT SR9,"-"] 
    [ STRSUB SR6,7,8]
    [ STRCAT SR25,SR0]    
    [ STRCAT SR9,SR0]
    [ Strcat SR9," " ]


    [ STRSUB SR6,9,10]
    [ Strcat SR9,SR0]
    [ ASSIGN SR26,SR0]
    [ STRCAT SR9,":"] 
    [ STRSUB SR6,11,12]
    [ STRCAT SR9,SR0]
    [ STRCAT SR26,SR0]
    [ STRCAT SR9,":"] 
    [ STRSUB SR6,13,14]
    [ STRCAT SR9,SR0 ]

      [ Strcat SR1, SR9 ]
	[ Strcat SR1,"','" ]
	[ Strcat SR1,SR9 ]
	[ Strcat SR1,"',1,'" ]
	[ Strcat SR1,SR8 ]
	[ Strcat SR1,"','0',0,0,11,'96689000')" ]
	[ ExecSQL SR1 ]
	Passed->stSetDBCntOKForFindForNewUser
	Failed->stResetOp
	Timeout->stResetOp

*/
:stResetOp
    [ Assign SR1,"update T_9668911_bak set PARAM='0',zh='' where TEL='" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stWelcome
    Failed ->stWelcome
    TimeOut->stWelcome


:stSetDBCntOKForFindForNewUser
   [ Assign IR21,0 ]

    [ Assign SR11,FD0 ]        //ȡ�Է�����
    [ ASSIGN SR1,"update T_JH9668911 set STATUS=" ]   //����״̬
    [ STRCAT SR1,"9999" ]                       //���Է�device����״̬��ʶ��
    [ Strcat SR1,",CONNECTDT='"]
    [ NowTime ]
    [ Strcat SR1,SR0 ]               //��ǰ����ʱ��
    [ Strcat SR1,"' where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stWaitOperatorMessage
    Failed ->stResetOp
    TimeOut ->stResetOp

:stWaitOperatorMessage
    [ ClearDTMF]
    [ SetDTMF "" ]
    [ PlayFile ".\vox\wel11forop.vox" ]
    StopPlayBack->stResetSelf
    DialTerminate->stResetSelf
    Failed->stResetSelf

    //CallTerm(0)->stStopWaitMusic
    CallTerm(1)->stSaveForOpernotGetCall
    CallTerm(2)->stSaveForOpernotGetCall
    CallTerm(3)->stSaveForOpernotGetCall
    CallTerm(4)->stSaveForOpernotGetCall
    CallTerm(5)->stSaveForOpernotGetCall
    //CallTerm->stResetSelf
    MessageGeted(0)->stStopWaitMusic



:stSaveForOpernotGetCall//���滰��Աδ�����绰�����wyfadd20110307
    [ Assign SR1,"insert into opgetcall (tel,optel,times) values('" ]
    [ Strcat SR1,$CallerID ]
    [ Strcat SR1,"','" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"','" ]
    [ NowTime ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1, "')" ]
    [ EXECSQL SR1 ]
    Passed ->stResetSelf
    Failed ->stResetSelf
    TimeOut->stResetSelf





/*    [ Timer 30 ]
    TimeOut->stResetSelf
    CallTerm->stStopWaitMusic
    MessageGeted(0)->stStopWaitMusic
*/
:stStopWaitMusic
	[ StopVoxAction ]
	StopAction->stTimer2
	MessageGeted(0)->stTimer2

:stResetSelf
   [ Assign IR21,0 ]

    [ Assign SR11,FD0 ]        //ȡ�Է�����
    [ ASSIGN SR1,"update T_JH9668911 set STATUS=" ]   //����״̬
    [ STRCAT SR1,"0" ]
    [ Strcat SR1," where USERCODE='" ]
    [ Strcat SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stResetOperator
    Failed ->stResetOperator
    TimeOut ->stResetOperator

:stResetOperator
    [ Assign SR1,"update T_9668911_bak set PARAM='0',ZH='' where TEL='" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stPlayContinuebb
    Failed ->stPlayContinuebb
    TimeOut->stPlayContinuebb

:stPlayContinuebb
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,"continuebb.vox" ]            //Ŀǰ���ȵ�����...
    [ PlayFile SR1 ]
    StopAction->stPlayMusic

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt

    StopPlayBack->stPlayMusic
    DialTerminate->stPlayMusic
    Failed->stPlayMusic


:stTimer2
    [ Timer 1 ]
    TimeOut->stGetOperatorDevice
	MessageGeted(0)->stGetOperatorDevice

:stGetOperatorDevice
    [ Assign SR1,"select DEVICEID,USERCODE from T_JH9668911 where USERCODE='"  ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stSetDBCntOKForFind
    Failed ->stResetSelf
    TimeOut ->stResetSelf


/*
    [ SetDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_cntxx ]   //��һλŮʿ
    [ PlayFile SR1 ]
    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stPlayCnt00
    Failed->stConnectDev
*/
////////////////////////////////////////////////////////////////////////////



:stSetDBCntOKForFind
   [ Assign IR21,0 ]

    [ StoI FD0 ]
    [ ASSIGN IR4,IR0 ]         //ȡ�Է���ʶ
    [ Assign SR11,FD1 ]        //ȡ�Է�����
    [ ASSIGN SR1,"update T_JH9668911 set STATUS=" ]   //����״̬
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]                       //���Է�device����״̬��ʶ��
    [ Strcat SR1,",CONNECTDT='"]
    [ NowTime ]
    [ Strcat SR1,SR0]               //��ǰ����ʱ��
    [ Strcat SR1,"',CONNECTCOUNT="]

    [ Strsub FD1,1,2 ]
    [ Equal SR0,"10" ]
    Passed->stOpConcount
    Failed->stAddIR26

:stAddIR26
    [ Inc IR26 ]
    ->stContinueUpdate

:stOpConcount
    [ Assign IR26,1 ]
    ->stContinueUpdate

:stContinueUpdate
    [ ItoS IR26 ]
    [ Assign $BillParam2,SR0 ]


    [ ItoS IR26 ]
    [ Strcat SR1,SR0 ]               //��ǰ���Ӵ���
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

:stPutMessage            //��Է�device������Ϣ
    [ ItoS $Device1 ]
    [ PutMessage IR4,IR2,SR0 ]
    ->stCheckSexForConnect

:stWelcome
    [ StrRight $InputParam,7 ] //wyfadd20101217for9668977���Ĳ���Ҫ���ŵ���
    [ Equal SR0,"9668977" ]
    Passed->stPlaywaitFor77
    Failed->stWelcome1

:stPlaywaitFor77
    [ PlayFile ".\vox\wait.vox" ]
    StopPlayBack->stPlayMusic
    DialTerminate->stPlayMusic
    Failed->stPlayMusic

:stWelcome1
    [ CallFunc &CheckTelyd ]
    Returned(1)->stCheckSex//1-��ͨ,
    Returned(2)->stCheckSex//2�ƶ�,
    Returned(3)->stCheckSex//3-����
    Returned->stCheckSex
:stWelcome1050720
    [ ClearDTMF]
    ->stCheckSex

    /*[ SetDTMF "#*" ]
    [ ASSIGN SR1,@vox_PromptPath ]
    [ ItoS IR2 ]
    [ StrCat SR1,SR0 ]
    [ STRCAT SR1,@vox_dy ]    //��ӭ����...
    [ PlayFile SR1 ]
    StopAction->stWelcome

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stCheckSex
    DialTerminate->stCheckSex
    Failed->stCheckSex*/
:stCheckSex
    [ Equal IR2,0]
    Passed->stWelcome00    //Ů
    Failed->stWelcome11    //��
:stWelcome00                   
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_dy00 ]            //����˵�����Ѿ��ǡ�����
    [ PlayFile SR1 ]
    StopAction->stWelcome00

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stPlayMusic
    DialTerminate->stPlayMusic
    Failed->stPlayMusic

:stWelcome11                   //����˵�����Ѿ��ǡ�����
    [ ASSIGN SR1,@vox_PromptPath] 
    [ STRCAT SR1,@vox_dy11]
    [ PlayFile SR1]
    StopAction->stWelcome11
    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stPlayMusic
    DialTerminate->stPlayMusic
    Failed->stPlayMusic

//�����������ǰ,�жϸ��û��Ƿ��³�ʱ,����������,�����򲥷�����:*�ż����Ժͱ�̨����С�㽻��
:stPlayMusic
    [ Equal IR2,0 ]///////////////////////////////////////
    Passed->stPlaymusic1    //Ů
    Failed->stPlayMusicaa    //��

:stPlayMusicaa
   [ ClearDTMF ]
//   [ CallFunc &JHUSERIN ]
//   Returned(0)->stPlayvox
//   Returned(0)->stPlaymusic1
//   Returned->stPlaymusic1
    ->stPlaymusic1
   
:stPlayvox
   [ Assign IR24,0 ]
   [ Assign SR1,".\vox\" ]
   [ Strcat SR1,@Vox_cantrans ]
   [ ClearDTMF ]
   [ SetDTMF "?" ]
   [ Playfile SR1 ]
    StopAction->stPlayMusic

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stPlayMusicnow
    DialTerminate->stGetDigit
    Failed->stPlayMusicnow

:stPlaymusic1
    [ Assign IR24,1 ]
    [ SetDTMF "#" ]     
    ->stPlayMusicnow

:stPlayMusicnow
    [ ClearDTMF ]
    [ SetDTMF "?" ]
//    [ IntRand @MusicCount ]
//    [ ItoS IR0 ]
//    [ ASSIGN SR1,@vox_MusicPath] 
//    [ STRCAT SR1,@vox_Music]
//    [ STRCAT SR1,SR0]
//    [ STRCAT SR1,".vox"]

    [ CallFunc &GetMusicPath ] 
    Returned->stPlayMusicNow2

:stPlayMusicNow2
    [ ClearDTMF ]
    [ SetDTMF "#" ]
    [ PlayFile SR1]
    StopAction->stPlayMusic

    MessageGeted(0)->stStopVox
    MessageGeted(1)->stStopVox
    MessageGeted(3)->stSetDBDeCnt 

    StopPlayBack->stGetDigit
    DialTerminate->stGetDigit
    Failed->stGetDigit


:stGetDigit
    [Digit 1]
    StopDigits('#')->stJudgeInput
    StopDigits('*')->stJudgeInput
    StopDigits->stJudgeInput
    TimeOut->stPlayMusicnow

:stJudgeInput
   [ Assign SR27,SR0 ]
   [ Equal IR24,0 ]
   Passed->stJudgeInput2
   Failed->stPlayMusicnow

:stJudgeInput2
   [ Equal SR27,"#" ]
   Passed->stPlayMusicnow
   Failed->stPlayMusicnow
//   Failed->stCheckSexForTans ������ת�˹�̨


:stCheckSexForTans
    [ Equal IR2,0 ]
    Passed->stPlayMusicnow
    Failed->stBillEndFor11

:stBillEndFor11
    [ ASSIGN SR1,"delete  T_JH9668911 where USERCODE='" ] 
    [ STRCAT SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stTransToAnother
    Failed ->stTransToAnother
    TimeOut ->stExit
    CallTerm->stExit
//    MessageGeted->stExit

:stTransToAnother
   [ CallFunc &GotoAnother ]/////////////////////////
   Returned->stClearDBFor160

:stClearDBFor160
    [ ASSIGN SR1,"delete  T_JH9668911 where USERCODE='" ] 
    [ STRCAT SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stExit
    Failed ->stExit
    TimeOut ->stExit


:stStopVox
    [ StoI $InputParam ]
    [ ASSIGN IR4,IR0 ]
    ->stSetDBCntOK

:stSetDBCntOK
   [ Assign SR1,"select USERCODE from T_JH9668911 where DEVICEID=" ]
   [ ItoS IR4 ]
   [ Strcat SR1,SR0 ]
   [ ExecSQL SR1 ]
   Passed->stSetDBCntOKa
   Failed->stSetDBCntOKa
   Timeout->stSetDBCntOKa

:stSetDBCntOKa
    [ Strsub FD0,1,2 ]
    [ Equal SR0,"10" ]//�Ƿ�Ϊ�������Ա�ĵ绰
    Passed->stSetDBCntOKb
    Failed->stAddIR26new
:stAddIR26new
    [ Inc IR26 ]
    ->stSetDBCntOKb

:stSetDBCntOKb
    [ ItoS IR26 ]
    [ Assign $BillParam2,SR0 ]

    [ ASSIGN SR1,"update T_JH9668911 set STATUS=" ]   //����״̬
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]                       //���Է�device����״̬��ʶ��
    [ Strcat SR1,",CONNECTDT='"]
    [ NowTime ]
    [ Strcat SR1,SR0]               //��ǰ����ʱ��
    [ Strcat SR1,"',CONNECTCOUNT="]
    [ ItoS IR26 ]
    [ Strcat SR1,SR0]               //��ǰ���Ӵ���
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
    [ STRCAT SR1,@vox_cntxx ]   //��һλŮʿ
    [ PlayFile SR1 ]
    MessageGeted(3)->stStopVoxBreak

    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stPlayCnt00
    Failed->stConnectDev
:stPlayCnt11
    [Equal $Operator,"" ]
    Passed->stPlayCnt11b
    Failed->stJudgeOpZhudongCall

:stJudgeOpZhudongCall
    [ Strlen $CallerID ]
    [ Less IR0,5 ]
    Passed->stPlayCnt11ForOp
    Failed->stPlayCnt11b

//081017wyfadd:play how long remain this user for operator
:stPlayCnt11ForOp
    [ PlayFile ".\vox\thisuser.vox" ]//���û��������ʱ��Ϊ
    StopPlayBack->stPlayHowLong
    DialTerminate->stPlayHowLong
    StopAction->stPlayHowLong
    Failed->stPlayHowLong
    MessageGeted(3)->stStopVoxBreak

:stPlayHowLong
    [ ParseParam SR10,4,"$" ]
    [ Mixplay 2,SR0 ]
    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stConnectDev
    Failed->stConnectDev
    MessageGeted(3)->stStopVoxBreak
//081017wyfaddend

:stPlayCnt11b
    [ SetDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath ] 
    [ STRCAT SR1,@vox_cntxy ]   //��һλ��ʿ
    [ PlayFile SR1 ]
    MessageGeted(3)->stStopVoxBreak

    StopPlayBack->stConnectDev
    DialTerminate->stConnectDev
    StopAction->stPlayCnt11
    Failed->stConnectDev
:stStopVoxBreak
    [ StopVoxAction ]
    StopAction->stSetDBDeCnt 
:stConnectDev                //���ӶԷ�
    [ Assign IR11,0 ]//��¼����ʱ��20050106
    [ ConnectDevice $Device1,IR4,2 ]
    Passed->stCheckOther    //�������Է��Ƿ�����
    Failed->stConnectVoxForFail
    MessageGeted(3)->stStopVoxBreak
:stConnectVoxForFail             //���������豸
    [ UnlistenDevice $Device1 ]
    [ ConnectDevice $VoxDevice,$Device1,2 ]
    Passed->stCheckFriend
    Failed->stCheckFriend
:stCheckOther          //���Է��Ƿ���������
    [ ASSIGN SR1,"select STATUS from T_JH9668911 where DEVICEID=" ]
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

:stCheckContainSelf            //���Է��Ƿ�����Լ�
    [ ItoS $Device1 ]
    [ Equal FD0,SR0 ]
    Passed->stCheckMRecord     //�Ѱ���
    Failed->stCheckOtherStatus
:stCheckOtherCountForStatus
    [ Great $DBRecCount ,0 ]
    Passed->stCheckOtherStatus 
    Failed->stSetDBDeCnt 
:stCheckOtherStatus
    [ Equal FD0,"0" ]
    Passed->stDelOtherSide
    Failed->stSetDBDeCnt
:stDelOtherSide       //����Է�״̬Ϊ0����ɾ���Է�
    [ ASSIGN SR1,"delete  T_JH9668911 where DEVICEID= " ]
    [ ItoS IR4 ]
    [ STRCAT SR1,SR0 ]
    [ EXECSQL SR1 ]
    Passed ->stSetDBDeCnt 
    Failed ->stSetDBDeCnt 
    TimeOut ->stSetDBDeCnt 
//¼��---------start
:stCheckMRecord
//   [ CallFunc &RountGet11 ]
 //  Returned(1)->stRecordForXYTC
 //  Returned(5)->stRecordForXYTC
 //  Returned->stSetTimer


//:stRecordForXYTC

    [ Equal IR25,0 ]    //�Ƿ���¼��
   Passed->stCheckSexForMRecord  //¼��
//   Passed->stSetTimer              //��¼��
    Failed->stSetTimer

:stCheckSexForMRecord    //Ů��Ҳ¼��
    [ Equal IR2,0 ]
    Passed ->stSetTimer  //Ů����¼��
   // Passed ->stStartRecordGirl    //Ů,��ʼ¼�����ļ������Girl��
    Failed ->stStartRecord   //��,��ʼ¼��

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

    [ ASSIGN SR20,"select USERCODE from T_JH9668911 where DEVICEID=" ]
    [ ItoS IR4 ]
    [ STRCAT SR20,SR0]
    [ EXECSQL SR20 ]
    Passed ->stStartRecord2
    Failed ->stStartRecord3
    TimeOut ->stStartRecord3

:stStartRecord2
    [ Strcat SR1,FD0 ]
    ->stStartRecord4
:stStartRecord3
    [ Strcat SR1,"0" ]
    ->stStartRecord4
:stStartRecord4
    [ STRCAT SR1,".vox" ]
    [ SetMRecord SR1 ]
    Passed->stSetTimerForRecord
    Failed->stSetTimer
:stStartRecordGirl
    [ ASSIGN SR1 , @vox_RecordPath ]
    [ STRCAT SR1 , "girl\" ]
    [ STRCAT SR1 , $CallerID ]
    [ STRCAT SR1 , "_" ]
    [ NowTime ]
    [ STRCAT SR1,SR0 ]
    [ STRCAT SR1,".vox" ]

    [ SetMRecord SR1 ]
    Passed->stSetTimerForRecord
    Failed->stSetTimer
      
:stSetTimerForRecord
    [ Assign IR25,1 ]   //¼����־

    [ TIMER @MaxRecordTime ]
    TimeOut->stStopMRecord
    MessageGeted(3)->stSetDBDeCnt

:stStopMRecord
    [ Add IR11,@MaxRecordTime ]
    [ StopMRecord ]   //ֹͣ˫��¼��
    ->stSetTimer
//¼��------end
:stSetTimer
    [ ClearDTMF ]
    [SetDTMF "#" ]
    [ Digit 20 ]
    TimeOut->stJudgeOpeTimer
    MessageGeted(3)->stSetDBDeCnt
    StopDigits('#')->stGetNextByDigit
    StopDigits->stJudgeOpeTimer

//20130502 add an jian da duan
:stGetNextByDigit
    [ PutMessage IR4,3,"" ]
    ->stSetDBDeCnt

//�ж��Ƿ���Ա����ͨ��ʱ�䳬ʱ
:stJudgeOpeTimer
    [ ParseParam SR10,1,"$" ]
    [ Equal SR0,"callout" ]
    Passed->stJudgeHowLong
    Failed->stCheckOther

:stJudgeHowLong
    [ Add IR11,20 ]
    [ Great IR11,@maxoplen ]
    Passed->stOpeTimeOut
    Failed->stCheckOther

:stOpeTimeOut
    [ PutMessage IR4,3,"" ]
    [ StopMRecord ]
    ->stBeforeExit

:stSetDBDeCnt    //�������ݿ�״̬--�Ͽ�����

    [ StopMRecord ]   //ֹͣ˫��¼��
    [ Assign IR25,0 ] 

    [ ASSIGN SR1,"update T_JH9668911 set STATUS=" ]
    [ STRCAT SR1,"0 where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ STRCAT SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stConnectVox
    Failed ->stConnectVox
    TimeOut ->stConnectVox

:stConnectVox               //���������豸
    [ UnlistenDevice $Device1 ]
    [ ConnectDevice $VoxDevice,$Device1,2 ]
    Passed->stJudgeForOpExit
    Failed->stJudgeForOpExit

:stJudgeForOpExit
    [ ParseParam SR10,1,"$" ]
    [ Equal SR0,"callout" ]
    Passed->stBeforeExit
    Failed->stJudgeNoVipTodayCon2


:stJudgeNoVipTodayCon2
    [ CallFunc &judgeTodayConCount ]
    Returned(0)->stBeforeExit
    Returned->stPlayDeCnt


:stPlayDeCnt
    [ setDTMF "" ]
    [ ASSIGN SR1,@vox_PromptPath] 
    [ STRCAT SR1,@vox_break]    //�Է��ѹһ�
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
    
    [ Assign SR1,"select STATUS from T_JH9668911 where DEVICEID=" ]
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
    [ ASSIGN SR1,"select CONNECTCOUNT from T_JH9668911 where USERCODE='" ]
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
    [ ASSIGN SR1,"delete  T_JH9668911 where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stResetOp
    Failed ->stResetOp
    TimeOut ->stResetOp

:stResetOp
    [ Assign SR1,"update T_9668911_bak set PARAM='0',ZH='' where TEL in('" ]
    [ ParseParam SR10,3,"$" ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,"','" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"')" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeNv
    Failed ->stJudgeNv
    TimeOut->stJudgeNv


:stJudgeNv
    [ Equal IR2,0 ]
    Passed->stExit
    Failed->stInsertJHUSER
//    Failed->stExit

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

    [ ASSIGN SR1,"select CONNECTCOUNT from T_JH9668911 where USERCODE='" ]
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
    [ ASSIGN SR1,"delete  T_JH9668911 where USERCODE='" ]
    [ STRCAT SR1,$CallerID ]
    [ Strcat SR1,"'" ]
    [ EXECSQL SR1 ]
    Passed ->stResetOp
    Failed ->stResetOp
    TimeOut ->stResetOp


:stResetOp
    [ Assign SR1,"update T_9668911_bak set PARAM='0',ZH='' where TEL in('" ]
    [ ParseParam SR10,3,"$" ]
    [ Strcat SR1,SR0 ]
    [ Strcat SR1,"','" ]
    [ Strcat SR1,SR7 ]
    [ Strcat SR1,"')" ]
    [ EXECSQL SR1 ]
    Passed ->stJudgeNv
    Failed ->stJudgeNv
    TimeOut->stJudgeNv

:stJudgeNv
    [ Equal IR2,0 ]
    Passed->stExit
    Failed->stInsertJHUSER
//Failed->stExit

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

GotoAnother
{
:stWait
    [ BillEnd ]
    Verify(0)->stJudgeInput
    Verify->stExit

:stJudgeInput
    [ Equal SR27,"*" ]
    Passed->stGotoRGT
    Failed->stGetNumber

:stGotoRGT
   [ Assign SR29,"9668960" ]
   ->stNowStart

:stGetNumber
   [ Assign SR29,"96689" ]
   [ StoI SR27 ]
   [ ParseParam "43$44$80$72$105$07$77$09$405$",IR0,"$" ]
   [ Strcat SR29,SR0 ]
   ->stNowStart

:stNowStart
   [ SINGLESTEPTRANSFER SR29,$CallerID,$BillParam1 ]
   Passed ->stExit
   Failed ->stExit

:stExit
   [ Return 0 ]
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
//ʡ���û�����1���򷵻�0
JudgeCaller
{
:stWait
      [ StrSub $BillParam1,1,8 ]
      [ Equal SR0,"96689003" ]//vip user
      Passed->stReturn1
	Failed->stReturn1
      //Failed->stJuageXAGH

:stJuageXAGH
	[ StrSub $CallerID,1,4 ]
      [ StrStr "$0298$0293",SR0 ]
      [ Great IR0,0 ]
	Passed->stReturn1
      Failed->stJudgeDSGH

:stJudgeDSGH
      [ StrSub $CallerID,1,6 ]
      [ StrStr "$029091",SR0 ]
      [ Great IR0,0 ]
	Passed->stReturn1
      Failed->stGetTel

:stGetTel
   [ StrSub $CallerID,4,20 ]
   [ Assign SR1,SR0 ]// 029****
   [ StrSub SR1,1,4 ]
   [ StrStr "$0298$0293",SR0 ]//xa gh 0290298***
   [ Great IR0,0 ]
   Passed->stJudgexa2quhaoGH
   Failed->stJudge2quhaoSJ

:stJudgexa2quhaoGH
    [StrLen SR1 ]
    [ Equal IR0,11 ]
    Passed->stReturn1
    Failed->stReturn0

:stJudge2quhaoSJ
    [ Equal SR0,"0291" ]//0290291***
    Passed->stAssign2quhaoSJ
    Failed->stSearch

:stAssign2quhaoSJ
    [ StrSub SR1,4,20 ]
    [ Assign SR1,SR0 ]
    ->stSearch

:stSearch
   [ Assign SR28,"select HD from T_ShanxiHD where charindex(HD,'" ]
   [ Strcat SR28,SR1 ]
   [ Strcat SR28,"')=1" ]
   [ ExecSQL SR28 ]
   Passed->stJudgeDBNum
   Failed->stPlayChTel
   TimeOut->stPlayChTel

:stJudgeDBNum
   [ Great $DBRecCount,0 ]
   Passed->stReturn1
   Failed->stPlayChTel

:stPlayChTel
   [ Assign $BillParam2,"wstel"]
   [ PlayFile ".\vox\wstel.vox" ]
    StopPlayBack->stReturn0
    DialTerminate->stReturn0
    Failed->stReturn0
    CallTerm->stReturn0

:stReturn1
   [ Assign IR29,1 ]
   ->stExit

:stReturn0
   [ Assign IR29,0 ]
   ->stExit

:stExit
   [ Return IR29 ]
}

judgeTodayConCount
{
:stWait
     [ StrStr "$9668900$96689000$",$CalledID ]
     [ Great IR0,0 ] //Ůʿ��ֱ���������ж�
     Passed->streturn1
     Failed->stJudgeOtherNvshiCalledID

:stJudgeOtherNvshiCalledID
    [ StrSub $CalledID,1,7 ]
    [ StrStr "$9668903$9668904$9668905$",SR0 ]
     [ Great IR0,0 ] //Ůʿ9668903*-9668905*��ֱ���������ж�
     Passed->streturn1
     Failed->stJudgeVip

:stJudgeVip
      [ StrSub $BillParam1,1,8 ]
      [ Equal SR0,"96689003" ]    //vip user not playfee 20150303
      Passed->streturn1
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

:stSearchNowMoth    //���ҵ��³��˽���Ĵ���
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
     [ Assign SR1,"delete from T_JH9668911 where USERCODE='" ]
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