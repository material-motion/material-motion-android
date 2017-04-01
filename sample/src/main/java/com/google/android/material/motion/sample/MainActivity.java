package com.google.android.material.motion.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  private Demo[] demos;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    demos = new Demo[]{
      new Demo("Tossable Tap", new Intent(this, TossableTapActivity.class)),
      new Demo("Tween", new Intent(this, TweenActivity.class)),
    };

    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(new DemoAdapter());
  }

  private static class Demo {
    public String text;
    public Intent intent;

    public Demo(String text, Intent intent) {
      this.text = text;
      this.intent = intent;
    }
  }

  private class DemoAdapter extends RecyclerView.Adapter<DemoViewHolder> {

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new DemoViewHolder(parent, getLayoutInflater());
    }

    @Override
    public void onBindViewHolder(DemoViewHolder holder, int position) {
      final Demo demo = demos[position];

      holder.text.setText(demo.text);
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          startActivity(demo.intent);
        }
      });
    }

    @Override
    public int getItemCount() {
      return demos.length;
    }
  }

  private class DemoViewHolder extends RecyclerView.ViewHolder {

    private final TextView text;

    public DemoViewHolder(ViewGroup parent, LayoutInflater inflater) {
      super(inflater.inflate(R.layout.demo_view, parent, false));

      text = (TextView) itemView.findViewById(R.id.text);
    }
  }
}
