<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

function checkSkin($filename) {
	$rows = array();
	$rows["action"] = "skin-check";

	$fp = fopen($filename, "rb");

	$fileVersion = int32Swap(readInt32($fp));
	if ($fileVersion == 0x534b494e) {
		$fileVersion = int32Swap(readInt32($fp));
		$rows["fileVersion"] = $fileVersion;
		$rows = loadSkinVersion20($fp, $rows);
		return $rows;
	}
	$rows["fileVersion"] = $fileVersion;
	if ($fileVersion == 12) {
		$rows = loadSkinVersion12($fp, $rows);
	} else if ($fileVersion = 13) {
		$rows = loadSkinVersion13($fp, $rows);
	} else {
		fclose($fp);
		$rows["valid"] = false;
		$rows["reason"] = "wrong file version " .$fileVersion;
		return $rows;
	}

	return $rows;
}

function loadSkinVersion12($fp , $rows) {
	$propsSize = int32Swap(readInt32($fp));
	$props = array();
	for ($i = 0; $i < $propsSize; $i++) {
		$propName = readStringAuto($fp);
		$propType = readByte($fp);
		$propData = "";
		if ($propType == 0) {
			$propData = readStringAuto($fp);
		} else if ($propType == 1) {
			$propData = int32Swap(readInt32($fp));
		} else if ($propType == 2) {
			$propData = readDouble($fp);
		} else if ($propType == 3) {
			$propData = readByte($fp);
		} else {
			$rows["valid"] = "false";
			$rows["reason"] = "error reading props";
			return $rows;
		}
		$props["$propName"] = $propData;
	}
	$rows["props"] = $props;
	$skinType = readStringAuto($fp);
	$rows["skinType"] = $skinType;
	fclose($fp);
	$rows["valid"] = "true";
	return $rows;
}

function loadSkinVersion13($fp , $rows) {
	readStringAuto($fp);//Head
	
	readStringAuto($fp);//Prop start
	
	$propsSize = int32Swap(readInt32($fp));
	
	
	$props = array();
	for ($i = 0; $i < $propsSize; $i++) {
		$propName = readStringAuto($fp);
		$propType = readByte($fp);
		$propData = "";
		if ($propType == 0) {
			$propData = readStringAuto($fp);
		} else if ($propType == 1) {
			$propData = int32Swap(readInt32($fp));
		} else if ($propType == 2) {
			$propData = readDouble($fp);
		} else if ($propType == 3) {
			$propData = readByte($fp);
		} else {
			$rows["valid"] = "false";
			$rows["reason"] = "error reading props";
			return $rows;
		}
		$props["$propName"] = $propData;
	}
	$rows["props"] = $props;
	readStringAuto($fp);//Prop end
	readStringAuto($fp);
	$skinType = readStringAuto($fp);
	$rows["skinType"] = $skinType;
	readStringAuto($fp);
	fclose($fp);
	$rows["valid"] = "true";
	return $rows;
}

function loadSkinVersion20($fp , $rows) {
	readInt32($fp);
	readInt32($fp);
	$skinType = readStringAuto($fp);
	$rows["skinType"] = $skinType;
	// check chunk format
	while (!feof($fp)) {
		$chunkSize = int32Swap(readInt32($fp));
		if ($chunkSize == 0) {
			fclose($fp);
			$rows["valid"] = "true";
			return $rows;
		}
		$chunkName = readString($fp, 4);
		$chunkFlags = int32Swap(readInt16($fp));
		if (!skip($fp, $chunkSize - 10)) {
			break
		}
	}
	fclose($fp);
	$rows["valid"] = "false";
	return $rows;
}

function readDouble($fp) {
	$data = fread($fp, 8);
	return unpack("d1", $data)[1];
}
function readInt64($fp) {
	$data = fread($fp, 8);
	return unpack("i1", $data)[1];
}
function readInt32($fp) {
	$data = fread($fp, 4);
	return unpack("i1", $data)[1];
}

function readInt16($fp) {
	$data = fread($fp, 2);
	return unpack("s1", $data)[1];
}

function readByte($fp) {
	$data = fread($fp, 1);
	return unpack("C1", $data)[1];
}

function readString($fp, $size) {
	$data = fread($fp, $size);
	return $data;
}

function readStringAuto($fp) {
	$size = int16Swap(readInt16($fp));
	return readString($fp, $size);
}
function readString($fp, $size) {
	if ($size == 0) {
		return "";
	}
	$data = fread($fp, $size);
	return $data;
}

function skip($fp, $size) {
	while ($size > 0 && !feof($fp)) {
		$tmp = min($size, 1024);
		fread($fp, $tmp);
		$size -= $tmp;
	}
	return $size == 0;
}

function int64Swap($int)
{
	$i1 = ($int >> 56) & 0xFF; 
	$i2 = ($int >> 48) & 0xFF; 
	$i3 = ($int >> 40) & 0xFF; 
	$i4 = ($int >> 32) & 0xFF; 
	$i5 = ($int >> 24) & 0xFF; 
	$i6 = ($int >> 16) & 0xFF;
	$i7 = ($int >> 8) & 0xFF;
	$i8 = ($int & 0xFF);
	return ($i8 << 56) + ($i7 << 48) + ($i6 << 40) + ($i5 << 32) + ($i4 << 24) + ($i3 << 16) + ($i2 << 8) + $i1;
}
function int32Swap($int)
{
	$i1 = ($int >> 24) & 0xFF; 
	$i2 = ($int >> 16) & 0xFF;
	$i3 = ($int >> 8) & 0xFF;
	$i4 = ($int & 0xFF);
	return ($i4 << 24) + ($i3 << 16) + ($i2 << 8) + $i1;
}
function int16Swap($int)
{
	$i1 = ($int >> 8) & 0xFF;
	$i2 = ($int & 0xFF);
	return ($i2 << 8) + $i1;
}
?>
