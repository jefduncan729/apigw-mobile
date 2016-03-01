package com.axway.apigw.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.axway.apigw.android.Constants;
import com.axway.apigw.android.R;
import com.axway.apigw.android.RequiredFieldException;
import com.axway.apigw.android.ValidationException;
import com.axway.apigw.android.api.TopologyModel;
import com.axway.apigw.android.model.ServerInfo;
import com.axway.apigw.android.util.Utilities;
import com.axway.apigw.android.view.ServiceViewHolder;
import com.vordel.api.topology.model.Group;
import com.vordel.api.topology.model.Host;
import com.vordel.api.topology.model.Service;

import java.util.List;

/**
 * Created by su on 2/23/2016.
 */
public class EditServiceFragment extends EditFrag<Service> implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText edName;
    private EditText edSvcPort;
    private EditText edMgmtPort;
    private ViewGroup caOpts;
    private RadioButton edSysCa;
    private RadioButton edUserCa;
    private RadioButton edExtCa;
    private RadioGroup edCaOpts;
    private AutoCompleteTextView edHost;
    private AutoCompleteTextView edGroup;
    private Spinner edAlg;
    private ViewGroup ctrSvcPort;
    private ViewGroup ctrHost;
    private ViewGroup ctrGroup;
    private boolean adding;
    private Group grp;
    private int signAlgNdx;
    private TopologyModel model = TopologyModel.getInstance();

    public static EditServiceFragment newInstance(Group grp, Service svc) {
        EditServiceFragment rv = new EditServiceFragment();
        rv.grp = grp;
        rv.item = svc;
        return rv;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.gtw_edit, null);
        edName = (EditText)rv.findViewById(R.id.edit_name);
        edSvcPort = (EditText)rv.findViewById(R.id.edit_svcs_port);
        edMgmtPort = (EditText)rv.findViewById(R.id.edit_mgmt_port);
        edHost = (AutoCompleteTextView)rv.findViewById(R.id.edit_host);
        edGroup = (AutoCompleteTextView)rv.findViewById(R.id.edit_group);
        ctrSvcPort = (ViewGroup)rv.findViewById(R.id.ctr_svcs_port);
        ctrHost = (ViewGroup)rv.findViewById(R.id.ctr_host);
        ctrGroup = (ViewGroup)rv.findViewById(R.id.ctr_group);
        caOpts = (ViewGroup) rv.findViewById(R.id.ctr_ca_opts);
        edCaOpts = (RadioGroup) rv.findViewById((R.id.ctr_ca_group));
        edSysCa = (RadioButton) rv.findViewById(R.id.edit_system_ca);
        edUserCa = (RadioButton) rv.findViewById(R.id.edit_user_ca);
        edExtCa = (RadioButton) rv.findViewById(R.id.edit_external_ca);
        adding = TextUtils.isEmpty(item.getId());
        edName.setText(item.getName());
        signAlgNdx = 0;
        if (item.getManagementPort() > 0)
            edMgmtPort.setText(String.format("%d", item.getManagementPort()));
        if (adding) {
            edUserCa.setChecked(false);
            edExtCa.setChecked(false);
            edSysCa.setChecked(true);
            List<String> hosts = model.getHostNames();
            String hnm = hosts.get(0);
            edHost.setEnabled(false);
            if (hosts.size() > 1) {
                ArrayAdapter<String> ha = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, hosts);
                ha.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                edHost.setAdapter(ha);
                edHost.setEnabled(true);
            }
            edHost.setText(hnm);
            List<String> grps = model.getGroupNames();
            String gnm = (grp == null ? grps.get(0) : grp.getName());
            edGroup.setEnabled(true);
            if (grps.size() > 1) {
                ArrayAdapter<String> ga = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, grps);
                ga.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                edGroup.setAdapter(ga);
            }
            edGroup.setText(gnm);
            edAlg = (Spinner) rv.findViewById(R.id.spinner01);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.signing_algorithms, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edAlg.setAdapter(adapter);
            show(ctrSvcPort, true);
            show(ctrHost, true);
            show(ctrGroup, true);
            show(caOpts, true);
        }
        else {
            show(ctrSvcPort, false);
            show(ctrHost, false);
            show(ctrGroup, false);
            show(caOpts, false);
        }
        return rv;
    }


    private void show(View v, boolean show) {
        if (v == null)
            return;
        v.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edName.addTextChangedListener(this);
        edMgmtPort.addTextChangedListener(this);
        if (adding) {
            edHost.addTextChangedListener(this);
            edGroup.addTextChangedListener(this);
            edSvcPort.addTextChangedListener(this);
            edSysCa.setOnClickListener(this);
            edUserCa.setOnClickListener(this);
            edExtCa.setOnClickListener(this);
            edAlg.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void populate(View view) {

    }

    @Override
    public void validate() throws ValidationException {
        if (TextUtils.isEmpty(edName.getText().toString()))
            throw new RequiredFieldException("Name");
        if (TextUtils.isEmpty(edMgmtPort.getText().toString()))
            throw new RequiredFieldException("Management Port");
        int mp = Utilities.strToIntDef(edMgmtPort.getText().toString(), 0);
        if (mp <= 0 || mp > 65535)
            throw new ValidationException(String.format("Management Port must be in range %s to %s", 1, 65535));
        if (adding) {
            if (TextUtils.isEmpty(edHost.getText().toString()))
                throw new RequiredFieldException("Host");
            if (TextUtils.isEmpty(edGroup.getText().toString()))
                throw new RequiredFieldException("Group");
            if (TextUtils.isEmpty(edSvcPort.getText().toString()))
                throw new RequiredFieldException("Services Port");
            int sp = Utilities.strToIntDef(edSvcPort.getText().toString(), 0);
            if (sp <= 0 || sp > 65535)
                throw new ValidationException(String.format("Services Port must be in range %s to %s", 1, 65535));
            if (mp == sp)
                throw new ValidationException("Management and Services port cannot be equal");
        }
    }

    @Override
    public void collect(Service item, Bundle extras) {
        String nm = edName.getText().toString();
        item.setName(nm);
        item.setManagementPort(Utilities.strToIntDef(edMgmtPort.getText().toString(), 0));
        if (adding) {
            extras.putString(Constants.EXTRA_HOST_NAME, edHost.getText().toString());
            extras.putString(Constants.EXTRA_GROUP_NAME, edGroup.getText().toString());
            extras.putInt(Constants.EXTRA_SERVICES_PORT, Utilities.strToIntDef(edSvcPort.getText().toString(), 0));
            extras.putInt(Constants.EXTRA_CA_OPTION, edCaOpts.getCheckedRadioButtonId());
            extras.putString(Constants.EXTRA_SIGN_ALG, (String)edAlg.getAdapter().getItem(signAlgNdx));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setDirty(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        setDirty(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (signAlgNdx != i) {
            signAlgNdx = i;
            setDirty(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
