package cn.ning.audiorecord.audiorecord.record;

public enum ActionState {
	FINISH , CANCEL 
}

enum CtrlButtonState{
	recorder , playing , stoped_Record , stoped_paly
}

enum StopReason{
	byUserOK , byUserCancel , tooLong , tooShort , error
}