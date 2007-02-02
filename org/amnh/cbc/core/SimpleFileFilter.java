package org.amnh.cbc.core;
/*
** File: SimpleFileFilter.java
** Author: Peter J. Erts (ersts@amnh.org)
** Creation Date: 2005-01-11
** Revision Date: 2005-01-11
**
** Version: 1.0
**
** Copyright (c) 2005, American Museum of Natural History. All rights reserved.
** 
** This library is free software; you can redistribute it and/or
** modify it under the terms of the GNU Library General Public
** License as published by the Free Software Foundation; either
** version 2 of the License, or (at your option) any later version.
** 
** This library is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Library General Public License for more details.
** 
** You should have received a copy of the GNU Library General Public
** License along with this library; if not, write to the
** Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
** MA 02110-1301, USA.
**
**/

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SimpleFileFilter extends FileFilter {
	
	private String extension;
	private String description;
	
	public SimpleFileFilter(String ext, String des) {
		extension = ext;
		description = des;
	}
	
	public boolean accept(File file) {
		if(file.isDirectory())
			return true;
		
		if(file.getName().toLowerCase().endsWith(extension.toLowerCase()))
			return true;
		
		return false;
	}
	
	public String getDescription() {
		return description;
	}
}
