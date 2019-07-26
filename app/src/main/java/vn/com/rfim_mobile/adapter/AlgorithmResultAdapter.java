package vn.com.rfim_mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import vn.com.rfim_mobile.R;
import vn.com.rfim_mobile.models.json.AlgorithmResult;
import vn.com.rfim_mobile.models.json.CellInfo;

import java.util.List;

public class AlgorithmResultAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<AlgorithmResult> algorithmResults;

    public AlgorithmResultAdapter(Context context, List<AlgorithmResult> algorithmResults) {
        this.context = context;
        this.algorithmResults = algorithmResults;
    }

    @Override
    public int getGroupCount() {
        return algorithmResults.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return algorithmResults.get(groupPosition).getCellInfos().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return algorithmResults.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return algorithmResults.get(groupPosition).getCellInfos().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AlgorithmResult algorithmResult = (AlgorithmResult) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_shelves, null);
        }
        TextView lvShelfId = convertView.findViewById(R.id.tv_shelf_id);
        lvShelfId.setText((groupPosition + 1) + ". Shelf " + algorithmResult.getShelfId());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        CellInfo cellInfo = (CellInfo) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_cells, null);
        }
        TextView tvCellId = convertView.findViewById(R.id.tv_cell_id);
        TextView tvCellQuantity = convertView.findViewById(R.id.tv_cell_quantity);
        TextView tvDate = convertView.findViewById(R.id.tv_date);
        tvCellId.setText(cellInfo.getCellId());
        tvCellQuantity.setText(cellInfo.getQuantity() + " Box");
        tvDate.setText(cellInfo.getDate());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
