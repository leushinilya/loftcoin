package com.leushinilya.loftcoin.ui.main.rates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leushinilya.loftcoin.LoftCoin;
import com.leushinilya.loftcoin.R;
import com.leushinilya.loftcoin.databinding.FragmentRatesBinding;

public class RatesFragment extends Fragment {

    FragmentRatesBinding binding;
    RatesViewModel ratesViewModel;
    RatesAdapter adapter;
    Menu menu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRatesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        adapter = new RatesAdapter();
        binding.ratesRecycler.setAdapter(adapter);
        binding.ratesRecycler.setLayoutManager
                (new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        ratesViewModel = new ViewModelProvider(this).get(RatesViewModel.class);
        ratesViewModel.liveDataCoins.observe(getViewLifecycleOwner(), coins -> adapter.setData(coins));

        ratesViewModel.currency.observe(getViewLifecycleOwner(), currency -> {
            ratesViewModel.getRemoteCoins(((LoftCoin) (getActivity().getApplication()))
                    .cmcAPI, ratesViewModel.currency.getValue());
            if(menu!=null && currency.equals("USD")) menu.getItem(0).setIcon(R.drawable.ic_usd);
            if(menu!=null && currency.equals("EUR")) menu.getItem(0).setIcon(R.drawable.ic_eur);
            if(menu!=null && currency.equals("RUB")) menu.getItem(0).setIcon(R.drawable.ic_rub);
        });

        ratesViewModel.isRefreshing.observe(getViewLifecycleOwner(),
                isRefreshing -> binding.ratesRefresh.setRefreshing(isRefreshing));
        binding.ratesRefresh.setOnRefreshListener(()
                -> ratesViewModel.getRemoteCoins(((LoftCoin) (getActivity().getApplication()))
                .cmcAPI, ratesViewModel.currency.getValue()));

        ratesViewModel.getRemoteCoins(((LoftCoin) (getActivity().getApplication()))
                .cmcAPI, ratesViewModel.currency.getValue());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.rates_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.currencyDialog) {
            CurrencyDialog dialog = new CurrencyDialog();
            dialog.show(getChildFragmentManager(), "");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}