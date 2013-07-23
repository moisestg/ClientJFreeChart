package es.upm.dit.com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PruebaMain {
		 
	public static void main(String args[]) throws IOException {
		
		//Cliente REST que obtiene los datos JSON a tratar
			
		URL url = new URL("http://safe-ridge-3499.herokuapp.com/json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
			 
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			+ conn.getResponseCode());
		}
			 
		BufferedReader br = new BufferedReader(new InputStreamReader(
		(conn.getInputStream())));
			 
		StringBuilder sb = new StringBuilder();
        String line;
		System.out.println("Output from Server .... \n");
		while ((line = br.readLine()) != null) {
			sb.append(line+"\n");
		}
		System.out.println(sb.toString());
		
		//Parseo del JSON
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(sb.toString()); 
		JSONArray medidas = json.getJSONArray("medidas");
		JSONObject cputimes = medidas.getJSONObject(0).getJSONObject("cputimes");
		System.out.print(cputimes);
		
		conn.disconnect();
			 		 
		
		//Tratamiento de los datos y generación de gráficas por medio de JFreeChart
		
		DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		
		for(int i=0;i<medidas.size();i++){
			for(int j=0; j<4; j++){
				dataset1.addValue(medidas.getJSONObject(i).getJSONObject("cputimes").getJSONArray("user").getLong(j), "User", "Medida "+(i+1)+" Core "+(j+1));
				dataset1.addValue(medidas.getJSONObject(i).getJSONObject("cputimes").getJSONArray("nice").getLong(j), "Nice", "Medida "+(i+1)+" Core "+(j+1));
				dataset1.addValue(medidas.getJSONObject(i).getJSONObject("cputimes").getJSONArray("idle").getLong(j), "Idle", "Medida "+(i+1)+" Core "+(j+1));
				dataset1.addValue(medidas.getJSONObject(i).getJSONObject("cputimes").getJSONArray("irq").getLong(j), "Irq", "Medida "+(i+1)+" Core "+(j+1));
				dataset1.addValue(medidas.getJSONObject(i).getJSONObject("cputimes").getJSONArray("sys").getLong(j), "Sys", "Medida "+(i+1)+" Core "+(j+1));
			}	
		}
		
		//Crear el gráfico...
		JFreeChart chart1 = ChartFactory.createBarChart("Monitorización Servidor Heroku (CPU Times)",
		"Medidas (Cada 1 min.)",
		"Segundos",
		dataset1, //Dataset
		PlotOrientation.VERTICAL,
		true,
		true,
		false);
		//crear y visualizar una ventana...
		ChartFrame frame1 = new ChartFrame("Monitorización cputimes", chart1);
		frame1.pack();
		frame1.setVisible(true);
		
		
		
		
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(int i=0;i<medidas.size();i++){
			dataset.addValue(((JSONObject)medidas.get(i)).getLong("freememory"), "Free Memory", ((JSONObject)medidas.get(i)).getString("time") );
		}
		
		//Crear el gráfico...
		JFreeChart chart = ChartFactory.createBarChart("Monitorización Servidor Heroku (Free Memory)",
		"Medidas (Cada 1 min.)",
		"Bytes",
		dataset, //Dataset
		PlotOrientation.VERTICAL,
		true,
		true,
		false);
		//crear y visualizar una ventana...
		ChartFrame frame = new ChartFrame("Monitorización freememory", chart);
		frame.pack();
		frame.setVisible(true);
	 
	}
}
