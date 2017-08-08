package com.example.AShell_ComLine;

import java.io.File;
import java.io.FilenameFilter;


class directoryfilefilter implements FilenameFilter{
	int Number_of_Files=0,Number_of_Directory=0;
	@Override
	public boolean accept(File dir, String filename) {
		// TODO Auto-generated method stub
		if(new File(dir.toString()+"/"+filename).isDirectory())
			Number_of_Directory++;
		else
			Number_of_Files++;
	   return true;
	}
}
