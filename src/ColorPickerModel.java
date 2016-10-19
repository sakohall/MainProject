import java.awt.Color;

public class ColorPickerModel {
	
	//Controller
	ColorController cpCtrl;
	
	//Attributes
	private float hue = 1.0f;
	private float saturation = 0.8f;
	private float brightness = 0.9f;
//	private Color mainColor = Color.RED;
	
	public void registerCtrl(ColorController c){
        cpCtrl = c;
    }
	
	public float getHue() {
		return hue;
	}
	
	public void setHue(float hue) {
		this.hue = hue;
		while(this.hue < 0) {
			this.hue += 1.f;
		}
	}
	
	public float getSaturation() {
		return saturation;
	}
	
	public void setSaturation(float saturation) {
		this.saturation = Math.max(saturation, 0.f);
		this.saturation = Math.min(this.saturation, 1.f);
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = Math.max(brightness, 0.f);
		this.brightness = Math.min(this.brightness, 1.f);
	}

	public Color getMainColor() {
		return Color.getHSBColor(this.hue, this.saturation, this.brightness);
	}
}