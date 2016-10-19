
public class ColorPickerModel {
	
	//Controller
	ColorPickerController cpCtrl;
	
	//Attributes
	private float hue = 0.0f;
	private float saturation = 0.0f;
	private float brightness = 0.0f;
	
	public void registerCtrl(ColorPickerController c){
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
	
}