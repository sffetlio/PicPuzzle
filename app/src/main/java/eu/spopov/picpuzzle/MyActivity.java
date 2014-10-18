package eu.spopov.picpuzzle;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyActivity extends Activity{

    private List<ImageView> imageViews = new ArrayList<ImageView>(16);
    private int[] randomIndexArray = new int[16];
    private static Context context;

    public static Context getAppContext() {
        return MyActivity.context;
    }

    private void ShuffleArray(int[] array)
    {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    private boolean checkSolved(){
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if((Integer) imageViews.get(j+i*4).getTag() != j+i*4){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivity.context = getApplicationContext();
        setContentView(R.layout.activity_my);

        ViewGroup root = (ViewGroup) findViewById(R.id.root);
        TypedArray allImages = getResources().obtainTypedArray(R.array.images);

        LayoutParams imageLayoutParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
        LayoutParams rowLayoutParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);

        // generate array with indexes 1,2,3,4...
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                randomIndexArray[j+i*4] = j+i*4;
            }
        }
        // shuffle the indexes and use them to randomly assign images
        ShuffleArray(randomIndexArray);

        for(int i = 0; i < 4; i++){
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(rowLayoutParams);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for(int j = 0; j < 4; j++){
                Drawable image = allImages.getDrawable(randomIndexArray[j+i*4]);
                ImageView img = new ImageView(this);
                img.setImageDrawable(image);
                img.setLayoutParams(imageLayoutParams);
                img.setAdjustViewBounds(true);
                img.setTag(j+i*4);
                imageViews.add(img);

                img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDrag(data, shadowBuilder, v, 0);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                img.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch(action) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                ImageView draggedView = (ImageView) event.getLocalState();
                                Drawable tmp = ((ImageView) v).getDrawable();
                                ((ImageView) v).setImageDrawable(draggedView.getDrawable());
                                draggedView.setImageDrawable(tmp);
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(checkSolved()){
                                    Toast.makeText(MyActivity.getAppContext(), "Solved", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;

                        }
                        return false;
                    }
                });

                rowLayout.addView(img);
            }

            root.addView(rowLayout);
        }

        allImages.recycle();
    }
}
