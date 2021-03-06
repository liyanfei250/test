package com.transaction.moshi.transactionplatform.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;


/**
 * Created by LYF on 2016/9/2.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentActivity activity;

    protected FragmentManager fragmentManager;

    /**
     * 重写父类方法, 初始化 activity  防止内存不足activity 被销毁 空指针异常
     * 同时重写onAttach(Activity activity)  不是V4包的Fragment onAttach(Context context) 不执行
     *
     * @param context ..
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FragmentActivity) context;
        fragmentManager = this.activity.getSupportFragmentManager();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (FragmentActivity) activity;
        fragmentManager = this.activity.getSupportFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        attachView();
        initView();
        loadData();
    }

    public abstract void attachView();

    protected abstract void initView();

    protected abstract void loadData();

    /**
     * 对各种控件进行设置、适配、填充数据
     */

    protected abstract int getLayoutId();

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        //根据 Tag 取消请求
        OkGo.getInstance().cancelTag(this);
    }

    protected void startFragment(int containId, Class<?> clz, boolean isAddBackStack) {
        startFragmentWithArgs(containId, clz, isAddBackStack, null);
    }

    protected void startFragmentWithArgs(int containId, Class<?> clz, boolean isAddBackStack, Bundle bundles) {
        startFragmentForResult(containId, clz, isAddBackStack, bundles, -1);
    }

    protected void startFragmentForResult(int containId, Class<?> clz, boolean isAddBackStack, Bundle bundles, int requestCode) {
        String TAG = clz.getSimpleName();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            try {
                System.out.println("创建新的实例");
                fragment = (Fragment) clz.newInstance();
                if (bundles != null) {
                    fragment.setArguments(bundles);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (bundles != null) {
            //fragment_transaction.setArguments(bundles);
            fragmentManager.beginTransaction().remove(fragment).commit();
            try {
                fragment = (Fragment) clz.newInstance();
                fragment.setArguments(bundles);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            System.out.println("重新创建实例, 添加参数");
        }


        if (fragmentManager.getFragments() != null && fragmentManager.getFragments().size() >= 1) {
            for (Fragment f : fragmentManager.getFragments()) {
                if (f != null) {
                    fragmentManager.beginTransaction()
                            .hide(f)
                            .commitAllowingStateLoss();
                }
            }
        }

        if (fragment != null && fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .show(fragment)
                    .commitAllowingStateLoss();

            return;
        }
        if (fragment != null && !fragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(containId, fragment, TAG)
                    .addToBackStack(isAddBackStack ? TAG : null)
                    .show(fragment)
                    .commitAllowingStateLoss();
        }
    }

    protected void startActivityForRes(Class TotalActivity,int flag) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        startActivityForResult(intent, flag);
    }
    protected void startActivity(Class TotalActivity) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        getActivity().startActivity(intent);
    }
    protected void startActivityWithData(Class TotalActivity,Bundle bundle) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }
    protected void startActivityForResultWithData(Class TotalActivity,Bundle bundle,int requestCode) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, requestCode);
    }
    protected void startActivityForResultWithData(Fragment fragment,Class TotalActivity,Bundle bundle,int requestCode) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent,requestCode);
    }
    public void startActivityForResult(Fragment fragment,Class TotalActivity,int requestCode) {
        Intent intent = new Intent(getActivity(), TotalActivity);
        fragment.startActivityForResult(intent,requestCode);
    }
}
