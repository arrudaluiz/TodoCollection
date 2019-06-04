package br.edu.utfpr.todocollection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private RecyclerView recyclerView;
    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    public interface OnItemClickListener {

        public void onItemClick(View view, int position);

        public void onItemLongClick(View view, int position);
    }

    public RecyclerItemClickListener(Context context,
                                     RecyclerView recycler,
                                     OnItemClickListener listener) {

        recyclerView = recycler;
        onItemClickListener = listener;

        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {

                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                        if (childView != null && onItemClickListener != null) {

                            onItemClickListener.onItemClick(childView,
                                    recyclerView.getChildAdapterPosition(childView));

                            return true;
                        }

                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                        View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                        if (childView != null && onItemClickListener != null) {

                            onItemClickListener.onItemLongClick(childView,
                                    recyclerView.getChildAdapterPosition(childView));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        return gestureDetector.onTouchEvent(e);
    }
}