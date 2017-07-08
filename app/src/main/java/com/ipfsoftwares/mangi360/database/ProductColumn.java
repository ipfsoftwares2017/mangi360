package com.ipfsoftwares.mangi360.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by twalipo on 7/8/17.
 */

public interface ProductColumn {
	@DataType(DataType.Type.INTEGER)
	@PrimaryKey
	@AutoIncrement
	String _ID = "_id";

	@DataType(DataType.Type.TEXT)
	@NotNull
	String NAME = "name";

	@DataType(DataType.Type.TEXT)
	@NotNull
	String PRICE = "price";

}
