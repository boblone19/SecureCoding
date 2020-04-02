<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_behavior="@string/appbar_scrolling_view_behavior"
    tools:context="com.securecodewarrior.kotlinandroidscwbank.gui.main.AccountDetailsActivity"
    tools:showIn="@layout/activity_account_details"
    android:orientation="vertical">
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <android.support.v7.widget.RecyclerView
        android:background="@color/white"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/rv_account_details_activity_statements"/>
</LinearLayout>
