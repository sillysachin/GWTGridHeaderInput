package com.appbootup.explore.gwt.client;

import java.util.Comparator;

import com.appbootup.explore.gwt.client.ContactDatabase.ContactInfo;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTGridHeaderInput implements EntryPoint
{
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT.create( GreetingService.class );

	DataGrid<ContactInfo> dataGrid;

	SimplePager pager;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		initDataGrid();
		// Add the CellList to the adapter in the database.
		ContactDatabase.get().addDataDisplay( dataGrid );
		RootLayoutPanel.get().add( dataGrid );
	}

	private void initDataGrid()
	{
		dataGrid = new DataGrid<ContactInfo>( ContactDatabase.ContactInfo.KEY_PROVIDER );
		dataGrid.setWidth( "100%" );
		dataGrid.setAutoHeaderRefreshDisabled( true );
		dataGrid.setEmptyTableWidget( new Label( "No Data is set." ) );
		// Attach a column sort handler to the ListDataProvider to sort the list.
		ListHandler<ContactInfo> sortHandler = new ListHandler<ContactInfo>( ContactDatabase.get().getDataProvider().getList() );
		dataGrid.addColumnSortHandler( sortHandler );
		// Create a Pager to control the table.
		SimplePager.Resources pagerResources = GWT.create( SimplePager.Resources.class );
		pager = new SimplePager( TextLocation.CENTER, pagerResources, false, 0, true );
		pager.setDisplay( dataGrid );

		// Add a selection model so we can select cells.
		final SelectionModel<ContactInfo> selectionModel = new MultiSelectionModel<ContactInfo>( ContactDatabase.ContactInfo.KEY_PROVIDER );
		dataGrid.setSelectionModel( selectionModel, DefaultSelectionEventManager.<ContactInfo> createCheckboxManager() );
		// Initialize the columns.
		initTableColumns( selectionModel, sortHandler );
	}

	private void initTableColumns( final SelectionModel<ContactInfo> selectionModel, ListHandler<ContactInfo> sortHandler )
	{
		// Checkbox column. This table will uses a checkbox column for selection.
		// Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable
		// mouse selection.
		Column<ContactInfo, Boolean> checkColumn = new Column<ContactInfo, Boolean>( new CheckboxCell( true, false ) )
		{
			@Override
			public Boolean getValue( ContactInfo object )
			{
				// Get the value from the selection model.
				return selectionModel.isSelected( object );
			}
		};
		dataGrid.addColumn( checkColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		dataGrid.setColumnWidth( checkColumn, 40, Unit.PX );

		// First name.
		Column<ContactInfo, String> firstNameColumn = new Column<ContactInfo, String>( new EditTextCell() )
		{
			@Override
			public String getValue( ContactInfo object )
			{
				return object.getFirstName();
			}
		};
		firstNameColumn.setSortable( true );
		sortHandler.setComparator( firstNameColumn, new Comparator<ContactInfo>()
		{
			@Override
			public int compare( ContactInfo o1, ContactInfo o2 )
			{
				return o1.getFirstName().compareTo( o2.getFirstName() );
			}
		} );
		dataGrid.addColumn( firstNameColumn, "First Name" );
		firstNameColumn.setFieldUpdater( new FieldUpdater<ContactInfo, String>()
		{
			@Override
			public void update( int index, ContactInfo object, String value )
			{
				// Called when the user changes the value.
				object.setFirstName( value );
				ContactDatabase.get().refreshDisplays();
			}
		} );
		dataGrid.setColumnWidth( firstNameColumn, 200, Unit.PX );
		// Empty Column.
		Column<ContactInfo, String> emptyColumn = new Column<ContactInfo, String>( new TextCell() )
		{
			@Override
			public String getValue( ContactInfo object )
			{
				return "";
			}
		};
		dataGrid.addColumn( emptyColumn, " " );
		dataGrid.setColumnWidth( emptyColumn, 20, Unit.PCT );
	}
}