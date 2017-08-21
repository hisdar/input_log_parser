// InstallDlg.cpp : ʵ���ļ�
//

#include "stdafx.h"
#include "HisdarInstaller.h"
#include "InstallDlg.h"
#include "afxdialogex.h"


#define BACKGROUD_TOP		40
#define BACKGROUD_LEFT		10
#define BACKGROUD_BOTTOM	220
#define BACKGROUD_RIGHT		527

#define LOG_ROW_SPACE		0

#define LOG_BORDER_LEFT		2
#define LOG_BORDER_TOP		2

// CInstallDlg �Ի���

IMPLEMENT_DYNAMIC(CInstallDlg, CDialogEx)

CInstallDlg::CInstallDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CInstallDlg::IDD, pParent)
	, logPool(DEFAULT_LOG_BUFFER_LEN)
	, installFlag(false)
{

}

CInstallDlg::~CInstallDlg()
{
}

void CInstallDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
	DDX_Control(pDX, IDC_PROGRESS_INSTALL, progressBar);
	DDX_Control(pDX, IDC_BUTTON_FINISH, finishButton);
}


BEGIN_MESSAGE_MAP(CInstallDlg, CDialogEx)
	
//	ON_WM_PAINT()
	ON_WM_PAINT()
	ON_BN_CLICKED(IDC_BUTTON_FINISH, &CInstallDlg::OnBnClickedButtonFinish)
	ON_WM_CLOSE()
END_MESSAGE_MAP()


// CInstallDlg ��Ϣ�������


void CInstallDlg::SetProcessValue(int value)
{
	progressBar.SetPos(value);
}

void CInstallDlg::OutputLine(CString log)
{
	CTime currentTime = CTime::GetCurrentTime();
	CString timeString = currentTime.Format("[%Y-%m-%d %H:%M:%S] ");

	timeString.Append(log);
	logPool.AddLog(timeString);

	InvalidateRect(CRect(BACKGROUD_LEFT, BACKGROUD_TOP, BACKGROUD_RIGHT, BACKGROUD_BOTTOM));
	UpdateWindow();
}

void CInstallDlg::SetProgressRange(int min, int max)
{
	progressBar.SetRange(min, max);
}



void CInstallDlg::OnPaint()
{
	PaintLog();
}

void CInstallDlg::OnBnClickedButtonFinish()
{
	PostQuitMessage(0);
}


void CInstallDlg::EnableFinishButton(bool enable)
{
	finishButton.EnableWindow(enable);
}


void CInstallDlg::PaintLog(void)
{
	if (logPool.GetLogCount() <= 0)
	{
		return;
	}

	//CDialogEx::OnPaint();
	CPaintDC dc(this); // device context for painting

	// ��ɫ������Size
	CRect backgroudRect(BACKGROUD_LEFT, BACKGROUD_TOP, BACKGROUD_RIGHT, BACKGROUD_BOTTOM);
	CBrush backgroudBrush;
	backgroudBrush.CreateSolidBrush(RGB(0, 0, 0));

	// ����һ����ɫ�ı���
	dc.FillRect(backgroudRect, &backgroudBrush);

	// �����ʵı������ó�͸��
	dc.SetBkMode(TRANSPARENT);
	dc.SetTextColor(RGB(0, 180, 0));

	// ����һ�����ֵĸ߶ȺͿ��
	CString logString = logPool.GetLogAt(0);
	CSize textSize = dc.GetTextExtent(logString);

	// ���㵱ǰ�����ܹ���ʾ������log
	int logRow = (BACKGROUD_BOTTOM - BACKGROUD_TOP) / (textSize.cy + LOG_ROW_SPACE);
	logRow = logRow > logPool.GetLogCount() ? logPool.GetLogCount() : logRow;

	TRACE(_T("Log Row:%d\n"), logRow);
	TRACE(_T("Line height:%d\n"), textSize.cy);

	int logLocationX = BACKGROUD_LEFT + LOG_BORDER_LEFT;
	int logLocationY = BACKGROUD_TOP  + LOG_BORDER_TOP;
	int textLength   = 0;

	// �������ֵ�����
	CFont textFont;
	textFont.CreatePointFont(90, _T("����"));
	dc.SelectObject(&textFont);

	// ��ʼ����log
	for (int i = 0; i < logRow; i++)
	{
		logString = logPool.GetLogAt(logPool.GetLogCount() - logRow + i);

		TRACE(_T("Draw To [%d,%d]\n"), logLocationX, logLocationY);
		//TRACE(_T("Draw String:%s\n" + logString));

		textLength = logString.GetLength();
		dc.TextOut(logLocationX, logLocationY, logString, textLength);

		logLocationY = logLocationY + textSize.cy + LOG_ROW_SPACE;
	}
}


void CInstallDlg::OnClose()
{
	if (installFlag)
	{
		return;
	}

	CDialogEx::OnClose();
	PostQuitMessage(0);
}
