#pragma once

class LogPool
{
public:
	LogPool(int bufferLenght);
	~LogPool(void);

private:
	CString *logBuffer;
	int endIndex;
	int bufferSize;
public:
	void AddLog(CString log);
	CString GetLogAt(int index);
	int GetLogCount(void);
private:
	int bufferLength;
};

