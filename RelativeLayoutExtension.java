package tomislavt.extensions.relativelayout;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.HVArrangement;
import com.google.appinventor.components.runtime.TableArrangement;

/**
 * Implementation of Relative Layout for arranging components.
 * 
 * @author tomsict@gmail.com (Tomislav Tomsic)
 *
 */
@DesignerComponent(version = 1, description = "<p>Implementation of Relative layout for "
		+ "arranging visible components on screen</p>", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "aiwebres/rl.png")
@SimpleObject(external = true)
public class RelativeLayoutExtension extends AndroidNonvisibleComponent {

	private RelativeLayout rootLayout;
	private ViewGroup.LayoutParams rootLayoutParams;

	private final Handler androidUIHandler = new Handler();

	private RelativeLayout.LayoutParams myComponentLp;
	private RelativeLayout.LayoutParams myPlacedComponentLp;
	private RelativeLayout.LayoutParams mySecondPlacedComponentLp;

	private ViewGroup rootParent;
	private final Activity context;
	private final Form form;

	int horizontalAlignment;
	int verticalAlignment;

	int zIndex;
	String name = "layer";
	// Backing for background color
	private int backgroundColor;

	public RelativeLayoutExtension(ComponentContainer container) {
		super(container.$form());

		context = container.$context();
		form = container.$form();

		rootLayout = new RelativeLayout(context);
		BackgroundColor(Component.COLOR_NONE);

		rootLayout.setId(100);
		setRootParent(null);

		rootLayoutParams = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		rootLayout.setLayoutParams(rootLayoutParams);
	}

	/**
	 * Returns the component's background color as an alpha-red-green-blue
	 * integer.
	 *
	 * @return background RGB color with alpha
	 */
	@SimpleProperty(category = PropertyCategory.APPEARANCE, description = "Returns the component's background color")
	public int BackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Specifies the button's background color as an alpha-red-green-blue
	 * integer. If the parameter is {@link Component#COLOR_DEFAULT}, the
	 * original beveling is restored. If an Image has been set, the color change
	 * will not be visible until the Image is removed.
	 *
	 * @param argb
	 *            background RGB color with alpha
	 */
	@DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_DEFAULT)
	@SimpleProperty(description = "Specifies the component's background color. "
			+ "The background color will not be visible if an Image is being displayed.")
	public void BackgroundColor(int argb) {
		backgroundColor = argb;
		getView().setBackgroundColor(argb);
	}

	public View getView() {
		return rootLayout;
	}

	public ViewGroup getRootParent() {
		return rootParent;
	}

	public void setRootParent(ViewGroup rootParent) {
		this.rootParent = rootParent;
	}

	@SimpleFunction(description = "Adds a visible component on a relative layout,"
			+ " whilst assuming that form (screen) is it's parent ."
			+ "Consequently, all properties from designer are preserved.")
	public void AddView(final AndroidViewComponent component) {
		horizontalAlignment = form.AlignHorizontal();
		verticalAlignment = form.AlignVertical();

		/*
		 * There seems to be problems if component is 100% in height and width,
		 * so here we are trying to correct that.
		 */
		if (component.getView().getWidth() == android.view.ViewGroup.LayoutParams.MATCH_PARENT
				&& component.getView().getHeight() == android.view.ViewGroup.LayoutParams.MATCH_PARENT) {

			myComponentLp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
		} else {
			myComponentLp = new RelativeLayout.LayoutParams(component.getView()
					.getLayoutParams().width, component.getView()
					.getLayoutParams().height);
		}

		// Placement
		if (component instanceof HVArrangement
				|| component instanceof TableArrangement) {

			if (horizontalAlignment == 1 && verticalAlignment == 1) {
				// Default case, putting it here for the code's
				// logical completness
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			}

			else if (horizontalAlignment == 1 && verticalAlignment == 2) {
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				myComponentLp.addRule(RelativeLayout.CENTER_VERTICAL);
			}

			else if (horizontalAlignment == 1 && verticalAlignment == 3) {
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}

			else if (horizontalAlignment == 2 && verticalAlignment == 1) {
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			}

			else if (horizontalAlignment == 2 && verticalAlignment == 2) {
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				myComponentLp.addRule(RelativeLayout.CENTER_VERTICAL);
			}

			else if (horizontalAlignment == 2 && verticalAlignment == 3) {
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}

			else if (horizontalAlignment == 3 && verticalAlignment == 1) {
				myComponentLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			}

			else if (horizontalAlignment == 3 && verticalAlignment == 2) {
				myComponentLp.addRule(RelativeLayout.CENTER_IN_PARENT);
			}

			else if (horizontalAlignment == 3 && verticalAlignment == 3) {
				myComponentLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				myComponentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			}
		}
		/*
		 * Here we are making the new View hierarchy
		 */
		final ViewGroup parent = (ViewGroup) component.getView().getParent();
		/*
		 * Parent should never be null, that is way nothing happens if it is.
		 */
		if (parent != null) {
			if (getRootParent() == null) {
				androidUIHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						parent.removeAllViews();
						setRootParent(parent);

						if (component.getView().getId() < 1) {
							int id = View.generateViewId();
							component.getView().setId(id);
						}

						component.getView().setLayoutParams(myComponentLp);
						rootLayout.addView(component.getView(), 0,
								myComponentLp);

						parent.addView(rootLayout, 0, rootLayoutParams);

						getView().invalidate();
						getView().requestLayout();
						getRootParent().invalidate();
						getRootParent().requestLayout();
					}
				}, 100);
				/*
				 * We are trying again in 1/10th of a sec. We are doing this
				 * becouse Java.
				 */

			}
			// Root parent is already present
			else {
				androidUIHandler.postDelayed(new Runnable() {

					@Override
					public void run() {

						parent.removeView(component.getView());

						if (component.getView().getId() < 1) {
							int id = View.generateViewId();
							component.getView().setId(id);
						}

						component.getView().setLayoutParams(myComponentLp);
						rootLayout.addView(component.getView(), myComponentLp);

						getView().invalidate();
						getView().requestLayout();

						getRootParent().invalidate();
						getRootParent().requestLayout();
					}
				}, 100);
			}
		}
	}// add view end

	@SimpleFunction(description = "Adds a visible component to relative layout, while specifying its position on screen,"
			+ "but holding informations, and child components created in designer."
			+ "Valid values for placements are from 1 to 23, in both cases. "
			+ "Any other number will simply be disregarded. "
			+"Example of use: "
			+ "Placement 1 stands for rule: android:layout_above, "
			+ "Placement 2 stands for rule: android:layout_alignBaseline, "
			+ "etc. following information available on: "
			+ "https://developer.android.com/reference/android/widget/RelativeLayout.LayoutParams.html. "
			+ "One is advised to consult documentation and various readily"
			+ " available examples of RelativeLayout and its parameters.")
	public void AddAndPlaceView(final AndroidViewComponent component,
			final int placement, final int placement2, final int anchor) {

		final ViewGroup parent = (ViewGroup) component.getView().getParent();

		if (parent != null) {
			if (getRootParent() == null) {
				androidUIHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (component.getView().getId() < 1) {
							int id = View.generateViewId();
							component.getView().setId(id);
						}
						mySecondPlacedComponentLp = createLP(component,
								placement, placement2, anchor);
						parent.removeAllViews();

						component.getView().setLayoutParams(
								mySecondPlacedComponentLp);
						rootLayout.addView(component.getView(), 0,
								mySecondPlacedComponentLp);

						parent.addView(rootLayout, 0, rootLayoutParams);
						setRootParent(parent);
						/*
						 * I hate the following code, but we simply have to make
						 * sure.
						 */
						getView().invalidate();
						getView().requestLayout();
						getRootParent().invalidate();
						getRootParent().requestLayout();

					}
				}, 100);
			} // root parent is not null
			else {
				androidUIHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (component.getView().getId() < 1) {
							int id = View.generateViewId();
							component.getView().setId(id);
						}
						mySecondPlacedComponentLp = createLP(component,
								placement, placement2, anchor);
						parent.removeView(component.getView());

						component.getView().setLayoutParams(
								mySecondPlacedComponentLp);
						rootLayout.addView(component.getView(), 0,
								mySecondPlacedComponentLp);
						/*
						 * Again, we are making sure
						 */
						getView().invalidate();
						getView().requestLayout();
						getRootParent().invalidate();
						getRootParent().requestLayout();
					}
				}, 100);
			}
		}
	}// Add And place View end

	private RelativeLayout.LayoutParams createLP(
			AndroidViewComponent component, int placement, int placement2,
			int anchor) {

		/*
		 * There are some problems when layout is made as 100% width and height
		 * in designer, with these if statments, we are trying to correct that.
		 * Tomi
		 */

		if (component.getView().getWidth() == android.view.ViewGroup.LayoutParams.MATCH_PARENT
				&& component.getView().getHeight() == android.view.ViewGroup.LayoutParams.MATCH_PARENT) {

			myPlacedComponentLp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
		} else {
			myPlacedComponentLp = new RelativeLayout.LayoutParams(component
					.getView().getLayoutParams().width, component.getView()
					.getLayoutParams().height);
		}

		switch (placement) {
		case 1:
			myPlacedComponentLp.addRule(RelativeLayout.ABOVE, anchor);
			break;
		case 2:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_BASELINE, anchor);
			break;
		case 3:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_BOTTOM, anchor);
			break;
		case 4:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_END, anchor);
			break;
		case 5:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_LEFT, anchor);
			break;
		case 6:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			break;
		case 7:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_END,
					RelativeLayout.TRUE);
			break;
		case 8:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			break;
		case 9:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			break;
		case 10:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_START,
					RelativeLayout.TRUE);
			break;
		case 11:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
			break;
		case 12:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_RIGHT, anchor);
			break;
		case 13:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_START, anchor);
			break;
		case 14:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_TOP, anchor);
			break;
		case 15:
			/*
			 * Problem1. How to get: android:layout_alignWithParentIfMissing in
			 * code? Problem 2: if I leave it out of this, then users following
			 * informations on: https://developer.android.com/reference/android
			 * /widget/RelativeLayout.LayoutParams.html will be mislead.
			 */
			myPlacedComponentLp.addRule(RelativeLayout.TRUE);
			break;
		case 16:
			myPlacedComponentLp.addRule(RelativeLayout.BELOW, anchor);
			break;
		case 17:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			break;
		case 18:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);
			break;
		case 19:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_VERTICAL, anchor);
			break;
		case 20:
			myPlacedComponentLp.addRule(RelativeLayout.END_OF, anchor);
			break;
		case 21:
			myPlacedComponentLp.addRule(RelativeLayout.LEFT_OF, anchor);
			break;
		case 22:
			myPlacedComponentLp.addRule(RelativeLayout.RIGHT_OF, anchor);
			break;
		case 23:
			myPlacedComponentLp.addRule(RelativeLayout.START_OF, anchor);
			break;
		}
		/*
		 * Sensles copy/paste, how I miss meta-pattern capable languages
		 */
		switch (placement2) {
		case 1:
			myPlacedComponentLp.addRule(RelativeLayout.ABOVE, anchor);
			break;
		case 2:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_BASELINE, anchor);
			break;
		case 3:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_BOTTOM, anchor);
			break;
		case 4:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_END, anchor);
			break;
		case 5:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_LEFT, anchor);
			break;
		case 6:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			break;
		case 7:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_END,
					RelativeLayout.TRUE);
			break;
		case 8:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			break;
		case 9:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
					RelativeLayout.TRUE);
			break;
		case 10:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_START,
					RelativeLayout.TRUE);
			break;
		case 11:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
			break;
		case 12:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_RIGHT, anchor);
			break;
		case 13:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_START, anchor);
			break;
		case 14:
			myPlacedComponentLp.addRule(RelativeLayout.ALIGN_TOP, anchor);
			break;
		case 15:
			/*
			 * To emphasize by repetition: Problem1. How to get:
			 * android:layout_alignWithParentIfMissing rule in code? Problem 2:
			 * if I leave it out of this, then users following informations on:
			 * https://developer.android.com/reference/android
			 * /widget/RelativeLayout.LayoutParams.html will be mislead.
			 */
			myPlacedComponentLp.addRule(RelativeLayout.TRUE);
			break;
		case 16:
			myPlacedComponentLp.addRule(RelativeLayout.BELOW, anchor);
			break;
		case 17:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			break;
		case 18:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_IN_PARENT,
					RelativeLayout.TRUE);
			break;
		case 19:
			myPlacedComponentLp.addRule(RelativeLayout.CENTER_VERTICAL, anchor);
			break;
		case 20:
			myPlacedComponentLp.addRule(RelativeLayout.END_OF, anchor);
			break;
		case 21:
			myPlacedComponentLp.addRule(RelativeLayout.LEFT_OF, anchor);
			break;
		case 22:
			myPlacedComponentLp.addRule(RelativeLayout.RIGHT_OF, anchor);
			break;
		case 23:
			myPlacedComponentLp.addRule(RelativeLayout.START_OF, anchor);
			break;
		}
		return myPlacedComponentLp;
	} // create LP end

	@SimpleFunction(description = "Return's component's anchor/id.")
	public int getViewId (AndroidViewComponent component) {
		return component.getView().getId();
	}
	
	@SimpleFunction(description = "Return's component's anchor/id.")
	public int getViewAnchor (AndroidViewComponent component) {
		return component.getView().getId();
	}
	// Remove view and refresh are to be implemented here.
}
