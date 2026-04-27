package com.sthenos.fortium.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sthenos.fortium.R;
import com.sthenos.fortium.model.entities.DistribucionMuscular;
import com.sthenos.fortium.model.entities.Ejercicio;
import com.sthenos.fortium.model.entities.Progreso1RM;
import com.sthenos.fortium.model.entities.ProgresoVolumen;
import com.sthenos.fortium.model.entities.Usuario;
import com.sthenos.fortium.ui.viewmodels.EjercicioViewModel;
import com.sthenos.fortium.ui.viewmodels.EntrenamientoViewModel;
import com.sthenos.fortium.ui.viewmodels.RutinaViewModel;
import com.sthenos.fortium.ui.viewmodels.UsuarioViewModel;
import com.sthenos.fortium.utils.Calculador1RM;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private LineChart chart;
    private PieChart pieChart;
    private BarChart barChart;
    private AutoCompleteTextView dropdownExercise;
    private EjercicioViewModel ejercicioViewModel;
    private EntrenamientoViewModel entrenamientoViewModel;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();
    private UsuarioViewModel usuarioViewModel;
    private Usuario usuario;
    private LiveData<List<Progreso1RM>> consultaActiva1RM;


    public StatisticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents(view);
        setupObservers();
        configurarEstiloGrafica();
        cargarEjercicios();
        cargarPieChart(view);
        cargarBarChart(view);
    }

    /**
     * Configura los observadores de los ViewModels.
     */
    private void setupObservers() {
        usuarioViewModel.getUsuarioActual().observe(getViewLifecycleOwner(), usuario -> {
            this.usuario = usuario;
        });
    }

    /**
     * Inicializa los componentes.
     * @param view
     */
    private void initComponents(@NonNull View view) {
        chart = view.findViewById(R.id.lineChart1RM);
        pieChart = view.findViewById(R.id.pieChartDistribution);
        barChart = view.findViewById(R.id.barChartVolume);
        dropdownExercise = view.findViewById(R.id.dropdownStatsExercise);
        ejercicioViewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);
        entrenamientoViewModel = new ViewModelProvider(requireActivity()).get(EntrenamientoViewModel.class);
        usuarioViewModel = new ViewModelProvider(requireActivity()).get(UsuarioViewModel.class);
    }

    /**
     * Configura el estilo de la gráfica de 1RM.
     */
    private void configurarEstiloGrafica() {
        chart.getDescription().setEnabled(false); // Quitar texto de abajo a la derecha
        chart.getLegend().setTextColor(Color.WHITE); // Leyenda en blanco
        chart.setNoDataText("Selecciona un ejercicio para ver tu progreso");
        chart.setNoDataTextColor(Color.LTGRAY);
        chart.animateX(1000); // Animacion de la gráfica al cargar

        // Eje X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setDrawGridLines(false);

        // Ejes Y
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false); // Solo mostramos números a la izquierda
    }

    /**
     * Carga todos los ejercicios en el desplegable. Cuando se elige uno, se calcula su rm de las sesiones.
     */
    private void cargarEjercicios() {
        // Pedimos todos los ejercicios para rellenar el desplegable
        ejercicioViewModel.getAllEjercicios().observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                listaEjercicios = ejercicios;

                // Extraer solo los nombres para el dropdown
                String[] nombres = new String[ejercicios.size()];
                for (int i = 0; i < ejercicios.size(); i++) {
                    nombres[i] = ejercicios.get(i).getNombre();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres);
                dropdownExercise.setAdapter(adapter);

                // Escuchar cuando el usuario elige un ejercicio
                dropdownExercise.setOnItemClickListener((parent, view, position, id) -> {
                    Ejercicio seleccionado = listaEjercicios.get(position);
                    cargarDatosEnGrafica(seleccionado.getId());
                });
            }
        });
    }

    /**
     * Carga los datos de la gráfica de 1RM.
     * @param ejercicioId ID del ejercicio seleccionado.
     */
    private void cargarDatosEnGrafica(int ejercicioId) {
        // Se hace de la siguiente forma por que por cada ejercicio que cambiemos para ver el RM, se hará una nueva consulta
        // y se quedará en segundo plano, asi que de esta forma nos encargamos de que solo haya un observe
        if (consultaActiva1RM != null) {
            consultaActiva1RM.removeObservers(getViewLifecycleOwner());
        }

        consultaActiva1RM = entrenamientoViewModel.getProgresion1RM(ejercicioId);

        consultaActiva1RM.observe(getViewLifecycleOwner(), progresos -> {
            if (progresos == null || progresos.isEmpty()) {
                chart.clear(); // Si nunca lo ha entrenado, vaciamos la gráfica
                chart.invalidate(); // Refrescar la gráfica
                return;
            }

            if (usuario == null) {
                chart.setNoDataText("Cargando perfil de usuario...");
                chart.invalidate();
                return;
            }

            ArrayList<Entry> puntos = new ArrayList<>();
            ArrayList<String> etiquetasFechas = new ArrayList<>();

            String genero = String.valueOf(usuario.getGenero());
            String fechaNacimiento = usuario.getFechaNacimiento();
            int edad = getEdad(fechaNacimiento);
//            double pesoCuerpo = usuario.getPesoActual();
//            double altura = usuario.getAltura();

            for (int i = 0; i < progresos.size(); i++) {
                Progreso1RM p = progresos.get(i);

                // Aplicamos la fórmula científica a cada punto de la gráfica
                // TODO: Lo del ajuste Antropométrico con (pesoCuerpo, altura)
                double valorCientifico = Calculador1RM.calcular1RMFinal(p.pesoMaximo, p.reps, p.rpe, genero, edad);
                puntos.add(new Entry(i, (float) valorCientifico));

                // Formateamos la fecha quitando la hora.
                etiquetasFechas.add(p.fecha.substring(0, 10));
            }

            // Damos estilo a la línea
            LineDataSet dataSet = new LineDataSet(puntos, "Peso Máximo (kg)");
            dataSet.setColor(Color.parseColor("#4CAF50"));
            dataSet.setCircleColor(Color.parseColor("#4CAF50"));
            dataSet.setLineWidth(3f);
            dataSet.setCircleRadius(5f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(10f);

            // Damos estilo a la gráfica entera.
            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.invalidate(); // Refrescar la gráfica
            chart.animateX(800); // Animar cada vez que cambian los datos

            // Configuramos el eje X poniendole las fechas.
            XAxis xAxis = chart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasFechas));
            xAxis.setGranularity(1f);
        });
    }

    /**
     * Calcula la edad a partir de la fecha de nacimiento.
     * @param fechaNacimiento Fecha de nacimiento en formato dd/MM/yyyy
     * @return Edad en años
     */
    private int getEdad(String fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaNac = LocalDate.parse(fechaNacimiento, formato);
        return Period.between(fechaNac, fechaActual).getYears();
    }

    /**
     * Carga la gráfica de distribución de musculos.
     * @param view
     */
    private void cargarPieChart(View view) {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaFiltro = hace30Dias.format(formatter);

        entrenamientoViewModel.getDistribucionMuscular30Dias(fechaFiltro).observe(getViewLifecycleOwner(), datos -> {
            ArrayList<PieEntry> entradas = new ArrayList<>();
            for (DistribucionMuscular d : datos) {
                entradas.add(new PieEntry(d.cantidadSeries, d.musculo));
            }

            PieDataSet dataSet = new PieDataSet(entradas, "");

            // Usamos una paleta de colores variada de Material Design
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(12f);

            // Damos estilo a la gráfica entera y la renderizamos con los datos obtenidos.
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.setEntryLabelColor(Color.WHITE);
            pieChart.getDescription().setEnabled(false);
            pieChart.animateY(1400);
            pieChart.invalidate();

            // Configuramos la leyenda (lo de abajo)
            Legend l = pieChart.getLegend();
            l.setTextColor(Color.WHITE); // Pone el texto en blanco
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);
            l.setEnabled(true);
        });
    }

    /**
     * Carga la gráfica de volumen.
     * @param view
     */
    private void cargarBarChart(View view) {
        // Estilo General
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.setDrawGridBackground(false);
        barChart.setNoDataText("Cargando volumen de entrenamiento..."); // Texto cuando no hay datos
        barChart.setNoDataTextColor(Color.LTGRAY);

        // Quitar los números de la derecha y dejar solo los de la izquierda
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setAxisMinimum(0f); // Para que las barras nazcan desde el 0 siempre

        // Estilo del Eje X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        // Cargar los Datos
        entrenamientoViewModel.getUltimas7SesionesVolumen().observe(getViewLifecycleOwner(), datos -> {
            if (datos == null || datos.isEmpty()) {
                barChart.clear();
                return;
            }

            ArrayList<BarEntry> entradas = new ArrayList<>();
            ArrayList<String> etiquetasFechas = new ArrayList<>();

            for (int i = 0; i < datos.size(); i++) {
                ProgresoVolumen pv = datos.get(i);

                // Añadimos el bloque Y
                entradas.add(new BarEntry(i, (float) pv.getTotalVolumen()));

                // Formateamos la fecha
                String fechaCorta = pv.getFecha();
                if (fechaCorta.length() > 5) {
                    fechaCorta = fechaCorta.substring(0, 10);
                    fechaCorta = fechaCorta.substring(8, 10) + "/" + fechaCorta.substring(5, 7);
                }
                etiquetasFechas.add(fechaCorta);
            }

            // Inyectamos las fechas en el Eje X
            xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasFechas));

            // Dar color y renderizar
            BarDataSet dataSet = new BarDataSet(entradas, "Tonelaje Semanal");
            dataSet.setColor(Color.parseColor("#2196F3"));
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueTextSize(10f);

            BarData barData = new BarData(dataSet);

            // Grosor de las barras
            barData.setBarWidth(0.7f);

            barChart.setData(barData);
            barChart.animateY(1200);
            barChart.invalidate();
        });
    }
}