package hitonoriol.stressstrain.util;

import java.util.ArrayList;
import java.util.List;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.TA_Grid;
import de.vandermeer.asciithemes.TA_GridConfig;

/* Allows to form AsciiTable rows cell by cell instead of adding the whole row at once */
public class Table extends AsciiTable {
	private final static TA_Grid DEFAULT_GRID = TA_Grid.create("Default")
			.addCharacterMap(TA_GridConfig.RULESET_NORMAL, '*', '-', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+')
			.addCharacterMap(TA_GridConfig.RULESET_HEAVY, '*', '=', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+');

	private List<Object> currentRow;

	public Table() {
		row(false);
		super.ctx.setGrid(DEFAULT_GRID);
	}

	public Table(Array2d contents, String... colNames) {
		this();
		addHeavyRule();
		for (String colName : colNames)
			add(colName);
		row(false);
		addHeavyRule();
		populate(contents);
	}

	/* Add cell to the current row */
	public Table add(Object obj) {
		currentRow.add(obj);
		return this;
	}

	/* Make remaining cells in the current row empty */
	public void expand() {
		int colDiff = getColNumber() - currentRow.size();
		if (colDiff > 0)
			for (int i = 0; i < colDiff; ++i)
				currentRow.add(null);
	}

	/* Begin a new row */
	public Table row(boolean addRule) {
		if (currentRow != null) {
			expand();
			super.addRow(currentRow);
			super.ctx.setWidth((super.ctx.getTextWidth() + currentRow.size() + 1));
		}
		if (addRule)
			addRule();
		currentRow = new ArrayList<>();
		return this;
	}
	
	public Table row() {
		return row(true);
	}

	/* Add all elemets from 2d array `arr` to this table */
	public void populate(Array2d arr) {
		for (int i = 0; i < arr.getHeight(); ++i) {
			for (int j = 0; j < arr.getWidth(); ++j) {
				add(arr.get(i, j));
			}
			row();
		}
	}
}