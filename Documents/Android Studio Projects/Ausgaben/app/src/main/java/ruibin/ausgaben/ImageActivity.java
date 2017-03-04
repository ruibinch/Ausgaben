package ruibin.ausgaben;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        setImage();
    }

    private void setImage() {
        ImageView imageView = (ImageView) findViewById(R.id.image);
        String imagePath = getIntent().getStringExtra("imagepath");
        System.out.println("imagepath = " + imagePath);

        if (imagePath != null) {
            try {
                File file = new File(imagePath);
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                imageView.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {
                Toast.makeText(this, "FileNotFoundException encountered", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
