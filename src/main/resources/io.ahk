#NoEnv  ; Recommended for performance and compatibility with future AutoHotkey releases.
; #Warn  ; Enable warnings to assist with detecting common errors.
SendMode Input  ; Recommended for new scripts due to its superior speed and reliability.
SetWorkingDir %A_ScriptDir%  ; Ensures a consistent starting directory.
if (%1%=="")
{    
    MsgBox This script is not meant to be run directly.
    ExitApp, [0]
}
^+g:: run java -cp %1% com.garby.hypixelstats.ui


