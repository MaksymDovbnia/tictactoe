package com.game.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.entity.Player;
import com.game.adapters.AdapterActivityList;
import com.game.adapters.MyOwnAdapter;
import com.utils.Loger;

public class ListTest extends Activity {

	ListView lvSimple;
	List<Player> playerList = new ArrayList<Player>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_test);
		for (int i = 1; i <= 50; i++) {
			Player player = new Player();
			player.setName("player " + i);
			playerList.add(player);
		}

		AdapterActivityList adapterActivityList = new AdapterActivityList(this,
				playerList);
		MyOwnAdapter myOwnAdapter = new MyOwnAdapter(this, playerList);
		// определяем список и присваиваем ему адаптер
		lvSimple = (ListView) findViewById(R.id.listView1);
		lvSimple.setAdapter(adapterActivityList);
		lvSimple.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Loger.printLog("click" + id + " p " + position);
				view.setBackgroundColor(Color.BLACK);
			}
		});
		lvSimple.setAdapter(adapterActivityList);

	}

}
