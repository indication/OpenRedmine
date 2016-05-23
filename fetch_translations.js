//var Source = WScript.Arguments.Item(0);
//var Target = WScript.Arguments.Item(1);
var settingTransifexProjectUrl = "https://www.transifex.com/projects/p/openredmine";
var settingTransifexClient = "http://files.transifex.com/transifex-client/0.10/tx.exe";
var settingTransifexHost = "https://www.transifex.com";


var objWS = WScript.CreateObject("WScript.Shell");
var pathRoot = objWS.CurrentDirectory;
d("CurrentDirectory",objWS.CurrentDirectory);

var objFSO = WScript.CreateObject('Scripting.FileSystemObject');
var pathWork = objFSO.BuildPath(pathRoot,"transifex");

//create working directory
if(!objFSO.FolderExists(pathWork)){
	d("CreateDirectory",pathWork);
	objFSO.CreateFolder(pathWork);
}

//fetch tx client
var pathTxClient = objFSO.BuildPath(pathWork,"tx.exe");
if(!objFSO.FileExists(pathTxClient)){
	d("Download",settingTransifexClient);
	if(!downloadFile(objFSO, settingTransifexClient ,pathTxClient)){
		e("Cannot download client. Abort",settingTransifexClient);
	}
	d("Download done",pathTxClient);
}

//create manifest to setup transifex
var pathBatch = objFSO.BuildPath(pathWork,"fetch.bat");
if (objFSO.FileExists(pathBatch)){
	d("Delete file",pathBatch);
	objFSO.DeleteFile(pathBatch);
}


//.tx folder exists, skip setup
var pathTxConfig = objFSO.BuildPath(pathWork,".tx");
var isSkipInit = "";
if(objFSO.FolderExists(pathTxConfig)){
	isSkipInit = "rem ";
}

var objTextFile = objFSO.CreateTextFile(pathBatch, true);
objTextFile.WriteLine("@echo off");
objTextFile.WriteLine("cd " + pathWork);
objTextFile.WriteLine(isSkipInit + "echo transifex initalize...");
objTextFile.WriteLine(isSkipInit + "tx init --host=" + settingTransifexHost);
objTextFile.WriteLine(isSkipInit + "echo.");
objTextFile.WriteLine(isSkipInit + "echo.");
objTextFile.WriteLine("echo setup transifex: " + settingTransifexProjectUrl);
objTextFile.WriteLine("tx set --auto-remote " + settingTransifexProjectUrl);
objTextFile.WriteLine("echo.");
objTextFile.WriteLine("echo.");
objTextFile.WriteLine("echo fetch data...");
objTextFile.WriteLine("tx pull  -a -s");
objTextFile.WriteLine("echo done. Press ANY key to continue");
objTextFile.WriteLine("pause");
objTextFile.WriteLine("");
objTextFile.Close();

//prepare to fetch data: clear local files
var pathTranslation = objFSO.BuildPath(pathWork,"translations");
if(objFSO.FolderExists(pathTranslation)){
	d("Delete folder force",pathTranslation);
	objFSO.DeleteFolder(pathTranslation,true);
}

//Exec tx!!!!!!
d("run batch",pathBatch);
objWS.Run("cmd /C " + pathBatch,1,true);
d("done batch",pathBatch);

//Check result
if(!objFSO.FolderExists(pathTranslation)){
	e("Folder is not exists. Failed to fetch data",pathTranslation);
}

//distribute translations to specific place
map(objFSO,pathTranslation+"/openredmine.strings_splashxml",pathRoot+"/OpenRedmine/src/main/res/values-XX","strings_splash.xml");
map(objFSO,pathTranslation+"/openredmine.strings_themesxml",pathRoot+"/OpenRedmine/src/main/res/values-XX","strings_themes.xml");
map(objFSO,pathTranslation+"/openredmine.stringsxml-49",pathRoot+"/OpenRedmine/src/main/res/values-XX","strings.xml");
map(objFSO,pathTranslation+"/openredmine.storemd",pathRoot+"/OpenRedmine/src/main/res/raw-XX","store.md");
map(objFSO,pathTranslation+"/openredmine.versionmd",pathRoot+"/OpenRedmine/src/main/res/raw-XX","version.md");
map(objFSO,pathTranslation+"/openredmine.contributorsmd",pathRoot+"/OpenRedmine/src/main/res/raw-XX","contributors.md");

//WScript.Echo("DONE!");


/* Download file
 * inherit from http://stackoverflow.com/questions/4164400/windows-script-host-jscript-how-do-i-download-a-binary-file
 */
function downloadFile(File,Source,Target){
	var Object = WScript.CreateObject('MSXML2.XMLHTTP');

	Object.Open('GET', Source, false);
	Object.Send();

	if (Object.Status != 200)
	{
		return false;
	}
	
	// Create the Data Stream
	var Stream = WScript.CreateObject('ADODB.Stream');

	// Establish the Stream
	Stream.Open();
	Stream.Type = 1; // adTypeBinary
	Stream.Write(Object.ResponseBody);
	Stream.Position = 0;

	// Create an Empty Target File
	if (File.FileExists(Target))
	{
		File.DeleteFile(Target);
	}

	// Write the Data Stream to the File
	Stream.SaveToFile(Target, 2); // adSaveCreateOverWrite
	Stream.Close();
	Object = null;
	Stream = null;
	return true;
	
}

function d(title,str){
	//WScript.Echo(title + ": " + str);
}
function e(title,str){
	WScript.Echo("ERROR! " + title + ": " + str);
	WScript.Quit();
}


function map(fso,pathFrom,pathToFoler,pathToFile){
	var pathTargetFolder = parsePath(fso, pathToFoler);
	var folder = fso.GetFolder(parsePath(fso, pathFrom));
	var folderfiles = new Enumerator(folder.Files);
	for (; !folderfiles.atEnd(); folderfiles.moveNext()){
		var pathFromFile = folderfiles.item();
		var itemname = fso.GetBaseName(pathFromFile).replace("_","-r");
		var pathSendFolder = pathTargetFolder.replace("XX",itemname);
		var pathSendFile = pathToFile.replace("XX",itemname);
		if(!fso.FolderExists(pathSendFolder)){
			fso.CreateFolder(pathSendFolder);
		}
		d("copy",pathFromFile);
		fso.CopyFile (pathFromFile,fso.BuildPath(pathSendFolder,pathSendFile), true);
	}
}

function parsePath(fso, pathToFolder){
	var pathTarget = "";
	var arrayPath = pathToFolder.split("/");
	for (var idx in arrayPath){
		var item = arrayPath[idx];
		if(item == ""){
		} else if(pathTarget == ""){
			pathTarget = item;
		} else {
			pathTarget = fso.BuildPath(pathTarget,item);
		}
	}
	return pathTarget;
}


