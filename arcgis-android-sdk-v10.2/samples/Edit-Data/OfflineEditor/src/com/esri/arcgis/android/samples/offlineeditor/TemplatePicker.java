package com.esri.arcgis.android.samples.offlineeditor;

import java.util.List;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.core.gdb.GdbFeature;
import com.esri.core.gdb.GdbFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.FeatureTemplate;
import com.esri.core.map.FeatureType;
import com.esri.core.renderer.Renderer;
import com.esri.core.symbol.Symbol;
import com.esri.core.symbol.SymbolHelper;
import com.esri.core.table.TableException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

public class TemplatePicker extends PopupWindow{
	Context context;
	MapView map;
	private FeatureLayer selectedLayer;	
	private FeatureTemplate selectedTemplate;
	private Symbol selectedSymbol;
	
	public TemplatePicker(Context ctx, MapView map) {
		// TODO Auto-generated constructor stub
		this.context = ctx;
		this.map = map;
		setContentView(populateContentView());
		Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay();
		setWidth(display.getWidth());
		setHeight(300);
        setFocusable(true);		
		setBackgroundDrawable(new BitmapDrawable());
	}
	
	
	public View populateContentView(){
		
		ScrollView scrollTemplateGroup = new ScrollView(context);
		scrollTemplateGroup.setVerticalScrollBarEnabled(true);
		scrollTemplateGroup.setVisibility(View.VISIBLE);
		scrollTemplateGroup.setScrollBarStyle(ScrollView.SCROLLBARS_INSIDE_INSET);
		
		LinearLayout templateGroup = new LinearLayout(context);
		templateGroup.setPadding(10, 10, 10, 10);
		templateGroup.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		templateGroup.setBackgroundResource(R.drawable.popupbg);
		templateGroup.setOrientation(LinearLayout.VERTICAL);
		
		
		
		for (Layer layer : map.getLayers()) {
			if (layer instanceof FeatureLayer) {
				RelativeLayout templateLayout = new  RelativeLayout(context);
				templateLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				
				TextView layerName = new TextView(context);
				layerName.setPadding(10, 10, 10, 10);
				layerName.setText(layer.getName());
				layerName.setTextColor(Color.MAGENTA);
				layerName.setId(1);
                
				RelativeLayout.LayoutParams layerTemplateParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layerTemplateParams.addRule(RelativeLayout.BELOW,layerName.getId());
				HorizontalScrollView scrollTemplateAndType = new HorizontalScrollView(context);
				scrollTemplateAndType.setPadding(5, 5, 5, 5);
				LinearLayout layerTemplate = new LinearLayout(context);
				layerTemplate.setBackgroundColor(Color.WHITE);
				layerTemplate.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                layerTemplate.setId(2);
                String typeIdField = ((GdbFeatureTable) ((FeatureLayer) layer).getFeatureTable()).getTypeIdField();
				
				if (typeIdField.equals("")) {
					List<FeatureTemplate> featureTemp = ((GdbFeatureTable) ((FeatureLayer) layer).getFeatureTable()).getFeatureTemplates();
					
						for (FeatureTemplate featureTemplate : featureTemp) {													
							GdbFeature g;
							try {
								g = ((GdbFeatureTable) ((FeatureLayer) layer).getFeatureTable()).createFeatureWithTemplate(featureTemplate,null);
								Renderer renderer = ((FeatureLayer) layer).getRenderer();
						        Symbol symbol =  renderer.getSymbol(g);					       
						       
						        Bitmap bitmap = createBitmapfromSymbol(symbol,(FeatureLayer)layer);
								
								populateTemplateView(layerTemplate,bitmap,featureTemplate,(FeatureLayer)layer,symbol);
							} catch (TableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					
					
				}
				else {
					List<FeatureType> featureTypes = ((GdbFeatureTable) ((FeatureLayer) layer).getFeatureTable()).getFeatureTypes();
					
					for (FeatureType featureType:featureTypes) {
						
						FeatureTemplate[] templates = featureType.getTemplates();
						for (FeatureTemplate featureTemplate : templates) {							
							GdbFeature g;
							try {
								g = ((GdbFeatureTable) ((FeatureLayer) layer).getFeatureTable()).createFeatureWithTemplate(featureTemplate,null);
								Renderer renderer = ((FeatureLayer) layer).getRenderer();
						        Symbol symbol =  renderer.getSymbol(g);
						        Bitmap bitmap = createBitmapfromSymbol(symbol,(FeatureLayer)layer);
						       
								populateTemplateView(layerTemplate,bitmap,featureTemplate,(FeatureLayer)layer,symbol);
							} catch (TableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}
						
					}
				}
				
				templateLayout.addView(layerName);
				scrollTemplateAndType.addView(layerTemplate);
				templateLayout.addView(scrollTemplateAndType, layerTemplateParams);
				templateGroup.addView(templateLayout);	
			}
			
		}
		scrollTemplateGroup.addView(templateGroup);
		
		return scrollTemplateGroup;		        
	}

	private Bitmap createBitmapfromSymbol(Symbol symbol,FeatureLayer layer) {
		Bitmap bitmap = null;
		if (layer.getGeometryType().equals(Geometry.Type.POINT)) {
			Point pt = new Point(20,20);
			bitmap = SymbolHelper.getLegendImage(symbol,pt, 50, 50,Color.WHITE);
		}
		else if (layer.getGeometryType().equals(Geometry.Type.POLYLINE)) {
			Polyline polyline = new Polyline();
			polyline.startPath(0,0);
			polyline.lineTo(40,40);			
			bitmap =  SymbolHelper.getLegendImage(symbol,polyline, 50, 50, Color.WHITE);
		}
		else if (layer.getGeometryType().equals(Geometry.Type.POLYGON)){
			Polygon polygon = new Polygon();
			polygon.startPath(0, 0);
			polygon.lineTo(40, 0);
			polygon.lineTo(40, 40);
			polygon.lineTo(0, 40);
			polygon.lineTo(0, 0);
			bitmap =  SymbolHelper.getLegendImage(symbol,polygon, 50, 50, Color.WHITE);
		}
		return bitmap;
	}

	private void populateTemplateView(
			LinearLayout layerTemplate,Bitmap bitmap,final FeatureTemplate featureTemplate,final FeatureLayer flayer,final Symbol symbol) {
		LinearLayout templateAndType = new LinearLayout(context);
		templateAndType.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		templateAndType.setOrientation(LinearLayout.VERTICAL);
		templateAndType.setPadding(10, 10, 10, 10);
		
		final ImageButton template = new ImageButton(context);
		template.setBackgroundColor(Color.WHITE);
		
		template.setImageBitmap(bitmap);
		template.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectedLayer = flayer;
				selectedTemplate = featureTemplate;				
				selectedSymbol = symbol;
				dismiss();
				//popup.dismiss();
				//map.removeAllGraphics();
				
			}
		});
		
		TextView templateType = new TextView(context);
		templateType.setText(featureTemplate.getName());
		templateType.setPadding(5, 5, 5, 5);
		templateAndType.addView(template);
		templateAndType.addView(templateType);

		LinearLayout.LayoutParams templateParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		templateParams.setMargins(20, 20, 20, 20);
		layerTemplate.addView(templateAndType, templateParams);
	}

	public FeatureLayer getSelectedLayer() {
		return selectedLayer;
	}

	public FeatureTemplate getselectedTemplate() {
	
		// TODO Auto-generated method stub
		return selectedTemplate;
	}
	
	public void clearSelection() {		
		// TODO Auto-generated method stub
		this.selectedTemplate = null;
		this.selectedLayer = null;
	}

	public Symbol getSelectedSymbol() {
		return selectedSymbol;
	}

   
}
