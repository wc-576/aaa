package com.jnu.student;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

//import com.jnu.student.adapter.TabPagerAdapter;
//import com.jnu.student.adapter.main_book;

import com.jnu.student.data.DataSaver;
import com.jnu.student.data.bookitem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    public static final int MENU_ID_ADD = 1;
    public static final int MENU_ID_UPDATE = 2;
    public static final int MENU_ID_DELETE = 3;
    private ArrayList<bookitem> bookitems;
    private MainRecycleViewAdapter mainRecycleViewAdapter;

    private ActivityResultLauncher<Intent> addDataLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            ,result -> {
                if(null!=result){
                    Intent intent=result.getData();
                    if(result.getResultCode()==AddBookItem.RESULT_CODE_SUCCESS)
                    {
                        Bundle bundle=intent.getExtras();
                        String title= bundle.getString("title");
                        String author= bundle.getString("author");
                        String publish= bundle.getString("publish");
                        String isbn= bundle.getString("isbn");
                        String bookshelf= bundle.getString("bookshelf");
                        double price=bundle.getDouble("price");
                        int position=bundle.getInt("position")+1;
                        Log.e("position",position+"");
                        bookitems.add(position, new bookitem(title,author,publish,isbn,bookshelf,price,R.drawable.book_no_name) );
                        new DataSaver().Save(this,bookitems);
                        mainRecycleViewAdapter.notifyItemInserted(position);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerViewMain=findViewById(R.id.booklist_recycler_view);

        DataSaver dataSaver = new DataSaver();
        bookitems = dataSaver.Load(this);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewMain.setLayoutManager(linearLayoutManager);


//        for(int i=1;i<20;++i)
//        {
//            bookitems.add(new bookitem("item "+i,Math.random()*10,i%2==1?R.drawable.book1:R.drawable.book2) );
//        }
        bookitems = new ArrayList<>();
        bookitems.add(new bookitem("book1 ","author1","tsinghua","1234567890","default",(double)100.0,R.drawable.book1));
        bookitems.add(new bookitem("book1 ","author2","jnu","1234567890","default",(double)100.0,R.drawable.book2));
        bookitems.add(new bookitem("book1 ","author3","alibaba","1234567890","default",(double)100.0,R.drawable.book3));
        bookitems.add(new bookitem("book1 ","author4","baidu","1234567890","default",(double)100.0,R.drawable.book4));
        mainRecycleViewAdapter= new MainRecycleViewAdapter(bookitems);
        recyclerViewMain.setAdapter(mainRecycleViewAdapter);
    }

    private ActivityResultLauncher<Intent> updateDataLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
            ,result -> {
                if(null!=result){
                    Intent intent=result.getData();
                    if(result.getResultCode()==AddBookItem.RESULT_CODE_SUCCESS)
                    {
                        Bundle bundle=intent.getExtras();
                        String title= bundle.getString("title");
                        String author= bundle.getString("author");
                        String publish= bundle.getString("publish");
                        String isbn= bundle.getString("isbn");
                        String bookshelf= bundle.getString("bookshelf");
                        double price=bundle.getDouble("price");
                        int position=bundle.getInt("position");
                        bookitems.get(position).setTitle(title);
                        bookitems.get(position).setTitle(author);
                        bookitems.get(position).setTitle(publish);
                        bookitems.get(position).setTitle(isbn);
                        bookitems.get(position).setTitle(bookshelf);
                        bookitems.get(position).setPrice(price);
                        new DataSaver().Save(this,bookitems);
                        mainRecycleViewAdapter.notifyItemChanged(position);
                    }
                }
            });

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case MENU_ID_ADD:

                Intent intent=new Intent(this, AddBookItem.class);
                intent.putExtra("position",item.getOrder());
                addDataLauncher.launch(intent);
//                bookitems.add(item.getOrder(),new bookitem("added"+item.getOrder(),Math.random()*10,R.drawable.book_no_name));
//                mainRecycleViewAdapter.notifyItemRangeInserted(item.getOrder(), 1);
                break;
            case MENU_ID_UPDATE:
//                bookitems.get(item.getOrder()).setTitle("updated 666");
//                mainRecycleViewAdapter.notifyItemChanged(item.getOrder());
                Intent intentUpdate=new Intent(this, AddBookItem.class);
                intentUpdate.putExtra("position",item.getOrder());
                intentUpdate.putExtra("title",bookitems.get(item.getOrder()).getTitle());
                intentUpdate.putExtra("author",bookitems.get(item.getOrder()).getAuthor());
                intentUpdate.putExtra("bookshelf",bookitems.get(item.getOrder()).getBookshelf());
                intentUpdate.putExtra("publish",bookitems.get(item.getOrder()).getPublish());
                intentUpdate.putExtra("price",bookitems.get(item.getOrder()).getPrice());
                updateDataLauncher.launch(intentUpdate);
                break;
            case MENU_ID_DELETE:
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("title")
                        .setMessage("are you sure to delete this item?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                bookitems.remove(item.getOrder());
                                mainRecycleViewAdapter.notifyItemRemoved(item.getOrder());
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                alertDialog.show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.ViewHolder> {

        private ArrayList<bookitem> localDataSet;

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            private final TextView textViewTitle;
            private final TextView textViewPrice;
            private final TextView textViewAuthor;
            private final TextView textViewBookshelf;
//            private final TextView textViewIsbn;
//            private final TextView textViewPublish;
            private final ImageView imageViewImage;

            public ViewHolder(View view) {
                super(view);
                // Define click listener for the ViewHolder's View
                imageViewImage = view.findViewById(R.id.iv_icon);
                textViewTitle = view.findViewById(R.id.tv_name);
                textViewPrice = view.findViewById(R.id.tv_price);
                textViewAuthor = view.findViewById(R.id.tv_author);
                textViewBookshelf = view.findViewById(R.id.tv_bookshelf);
//                textViewIsbn = view.findViewById(R.id.tv_price);
//                textViewPublish = view.findViewById(R.id.tv_price);

                view.setOnCreateContextMenuListener(this);
            }

            public TextView getTextViewAuthor() {
                return textViewAuthor;
            }
            public TextView getTextViewBookshelf() {
                return textViewBookshelf;
            }
//            public TextView getTextViewIsbn() {
//                return textViewIsbn;
//            }
//            public TextView getTextViewPublish() {
//                return textViewPublish;
//            }
            public TextView getTextViewPrice() {
                return textViewPrice;
            }
            public TextView getTextViewTitle() {
                return textViewTitle;
            }
            public ImageView getImageViewImage() {
                return imageViewImage;
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(0,MENU_ID_ADD,getAdapterPosition(),"Add "+getAdapterPosition());
                contextMenu.add(0,MENU_ID_UPDATE,getAdapterPosition(),"Update "+getAdapterPosition());
                contextMenu.add(0,MENU_ID_DELETE,getAdapterPosition(),"Delete "+getAdapterPosition());
            }
        }

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used
         * by RecyclerView.
         */
        public MainRecycleViewAdapter(ArrayList<bookitem> dataSet) {
            localDataSet = dataSet;
        }

        // Create new views (invoked by the layout manager)
        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view, which defines the UI of the list item
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_book, viewGroup, false);

            return new ViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.getTextViewTitle().setText("Name: "+localDataSet.get(position).getTitle());
            viewHolder.getTextViewAuthor().setText("Author: "+localDataSet.get(position).getAuthor());
            viewHolder.getTextViewBookshelf().setText("Bookshelf: "+localDataSet.get(position).getBookshelf());

            viewHolder.getTextViewPrice().setText("Price: "+localDataSet.get(position).getPrice().toString());

            viewHolder.getImageViewImage().setImageResource(localDataSet.get(position).getResourceId());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }
}