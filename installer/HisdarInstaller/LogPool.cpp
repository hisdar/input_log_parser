#include "StdAfx.h"
#include "LogPool.h"


LogPool::LogPool(int bufferLength)
	: endIndex(0)
	, bufferSize(0)
	, bufferLength(0)
{
	logBuffer = new CString[bufferLength];
	this->bufferLength = bufferLength;
}

LogPool::~LogPool(void)
{
	if (logBuffer != NULL)
	{
		delete[] logBuffer;
		logBuffer = NULL;
	}
}

void LogPool::AddLog(CString log)
{
	if (bufferSize < bufferLength)
	{
		bufferSize += 1;
	}

	endIndex += 1;
	endIndex = endIndex % bufferLength;

	logBuffer[endIndex] = log;
}

CString LogPool::GetLogAt(int index)
{
	if (index > bufferSize - 1)
	{
		return NULL;
	}

	int startIndex = endIndex - bufferSize + 1;
	if (startIndex < 0) {
		startIndex += bufferLength;
	}

	index = startIndex + index;
	index = index % bufferLength;

	return logBuffer[index];
}

int LogPool::GetLogCount(void)
{
	return bufferSize;
}
