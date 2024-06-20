package com.demo.services;

import com.demo.entities.*;
import com.demo.entities.Estados.EstadoEquipo;
import com.demo.entities.Estados.EstadoServidor;
import com.demo.entities.Estados.Eventos;
import com.demo.entities.Estados.Trabajo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionPractica extends Simulacion {

    //Desplegar para ver informacion de los atributos de abajo
    /**
     * PARAMETROS DE LA SIMULACION
     * - tiempoSimulacion: Tiempo de simulacion en horas.
     * - probabilidadesTipoTrabajo: Probabilidades de los diferentes tipos de trabajo (A,B,C,D).
     * - tiemposMediaTrabajo: Tiempos medios de ejecucion de los diferentes tipos de trabajo (A,B,C,D).
     * - limite_inferiorUniforme: Limite inferior de la distribucion uniforme del tiempo de trabajo.
     * - limite_superiorUniforme: Limite superior de la distribucion uniforme del tiempo de trabajo.
     * - tiempoDesdeInicioEquipoC: Tiempo desde que inicia el trabajo C hasta que puede ser dejado solo.
     * - tiempoAntesFinEquipoC: Tiempo antes de terminar el trabajo C en el que hay que retomarlo.
     * - tiempoInicioResultado: Tiempo desde el que empieza a guardar filas del vector para devolver.
     * - cantidadItercaciones: Cantidad de iteraciones que devuelve a partir del tiempoInicioResultado.
     */
    private double tiempoSimulacion;
    private ArrayList<Double> probabilidadesTipoTrabajo;
    private ArrayList<Double> tiemposMediaTrabajo;
    private double limite_inferiorUniforme;
    private double limite_superiorUniforme;
    private double tiempoDesdeInicioEquipoC;
    private double tiempoAntesFinEquipoC;
    private double tiempoInicioResultado;
    private int cantidadItercaciones;

    //Desplegar para ver informacion de los atributos de abajo
    /**
     * - tipoTrabajos: Tipos de trabajos que se pueden realizar.
     * - vectorDeEstados: Vector de estados de la simulacion, almacena una fila por iteracion a partir
     *      del "tiempoInicioResultado" y hasta alcanzar un tamaño de "cantiadaIteraciones".
     * - equipos: Lista de equipos que ingresaron al sistema, son eliminados una vez que salen.
     * - colaComun: Cola de equipos que ingresaron al sistema y esperan ser atendidos por primera vez.
     * - colaTrabajosC: Cola de equipos a los que se le realiza un trabajo C, fueron atendidos, dejados en segundo plano,
     *      y al momento de retomar su atencion el servidor estaba ocupado, por lo tanto esperan en esta cola a que termine
     *      lo que este haciendo y pueda finalizar su atencion.
     * - proximosEventos: Lista de los proximos eventos a ocurrir en la simulacion, en cada iteracion se extrae el que
     *      ocurre y se añade el proximo si es que hay.
     * - filaActual: Es la fila que se crea al finalizar la ejecucion del evento correspondiente a la iteracion actual,
     *      se añade al vector de estados al finalizar el evento actual.
     * - filaAnterior: Al inicio de la iteracion, el elemento que está en "filaActual", corresponde a la fila generada
     *      en la iteracion anterior, por lo tanto pasa a esta variable y sirve para mantener en la proxima fila a generar
     *      los valores que deban mantenerse de la fila de la interacion anterior.
     * - contadorIteraciones: Contador de las iteraciones que se han realizado en la simulacion, sirve para poner
     *      finalizar la simulacion si se alcanzan las 100000 iteraciones.
     * - contadorIteracionesResultado: Contador de las iteraciones que sirve para determinar cuantas filas
     *      del vector de estados guardar en el objeto a devolver a partir del tiempoInicioResultado.
     * - reloj: Tiempo actual de la simulacion.
     * - proximoEvento: Proximo evento a ocurrir en la simulacion, se extrae en cada iteracion del array de
     *      proximosEventos y los datos del evento (Hora, Equipo involucrado, Tipo de evento).
     * - contadorEquipos: Contador de los equipos que han ingresado al sistema, sirve para asignar un id a cada equipo y
     *      llevar un conteo para las estadisticas.
     * */

    private ArrayList<Trabajo> tipoTrabajos = new ArrayList<>(Arrays.asList(Trabajo.values()));
    private ArrayList<FilaVector> vectorDeEstados = new ArrayList<>();
    private ArrayList<Equipo> equipos = new ArrayList<>();
    private ArrayList<Equipo> colaComun = new ArrayList<>();
    private ArrayList<Equipo> colaTrabajosC = new ArrayList<>();
    private ArrayList<Evento> proximosEventos = new ArrayList<>();
    private FilaVector filaActual = null;
    private FilaVector filaAnterior = null;
    private int contadorIteraciones = 0;
    private int contadorIteracionesResultado = 1;

    private double reloj = 0;
    private Evento proximoEvento = null;
    private int contadorEquipos = 0;


    private void buscarProximoEvento() {

        //*
        // Busca el proximo evento a ocurrir en la simulacion, se recorre la lista de proximos eventos
        // y se van comparando en base a la diferencia entre la hora de ocurrencia y el reloj actual, por ende se
        // selecciona el que tenga la menor diferencia entre su hora de ocurrencia y el reloj actual,
        // es decir el proximo a ocurrir. Luego ese evento se remueve ese evento de la lista y se asigna a la variable
        // */.

        Evento proximoEvento = null;
        Optional<Evento> proxEvento = proximosEventos.stream()
                .min(Comparator.comparing(evento ->
                        evento.getHoraEvento() - this.reloj));
        if(proxEvento.isPresent()){
            proximoEvento = proxEvento.get();
//        for (int i = 0; i <= proximosEventos.size() - 1; i++) {
//            if (i == 0) {
//                tiempoProximoEvento = proximosEventos.get(i).getHoraEvento() - reloj;
//                proximoEvento = proximosEventos.get(i);
//            } else {
//                if (proximosEventos.get(i).getHoraEvento() - reloj < tiempoProximoEvento) {
//                    tiempoProximoEvento = proximosEventos.get(i).getHoraEvento() - reloj;
//                    proximoEvento = proximosEventos.get(i);
//                }
//            }
        }
        this.proximosEventos.remove(proximoEvento);
        this.proximoEvento = proximoEvento;
    }

    public FilasPaginadas getFilasPaginadas(Integer page) {
        // Sirve para extraer del vector de estados las filas que se deben devolver en la vista, en base a la
        // pagina que se solicita.
        FilasPaginadas filasPaginadas = new FilasPaginadas();
        FilaVector ultimaFila = this.vectorDeEstados.getLast();
        if (this.vectorDeEstados.size() > 200) {
            int fromIndex = page * 200;
            int toIndex = Math.min((page + 1) * 200, this.vectorDeEstados.size());
            filasPaginadas.setFilas(this.vectorDeEstados.subList(fromIndex, toIndex));
        } else {
            filasPaginadas.setFilas(this.vectorDeEstados);
        }
        if (!filasPaginadas.getFilas().contains(ultimaFila)) {
            filasPaginadas.getFilas().add(ultimaFila);
        }
        if (filasPaginadas.getFilas().getFirst() == ultimaFila) {
            filasPaginadas.getFilas().remove(ultimaFila);
            filasPaginadas.getFilas().add(ultimaFila);
        }
        return filasPaginadas;
    }

    public ResultadosSimulacion cola(double tiempo_simulacion,
                                     ArrayList<Double> probabilidadesTipoTrabajo,
                                     ArrayList<Double> tiemposMediaTrabajo,
                                     double limite_inferiorUniforme,
                                     double limite_superiorUniforme,
                                     double tiempoDesdeInicioEquipoC,
                                     double tiempoAntesFinEquipoC,
                                     double tiempoInicioResultado,
                                     int cantidadItercaciones,
                                     double Auniforme,
                                     double Buniforme,
                                     double numero_ingresado,
                                     double numero_ingresado_elevado) {

        this.tiempoSimulacion = tiempo_simulacion;
        this.probabilidadesTipoTrabajo = probabilidadesTipoTrabajo;
        this.tiemposMediaTrabajo = tiemposMediaTrabajo;
        this.limite_inferiorUniforme = limite_inferiorUniforme / 60;
        this.limite_superiorUniforme = limite_superiorUniforme / 60;
        this.tiempoDesdeInicioEquipoC = tiempoDesdeInicioEquipoC / 60;
        this.tiempoAntesFinEquipoC = tiempoAntesFinEquipoC / 60;
        this.tiempoInicioResultado = tiempoInicioResultado;
        this.cantidadItercaciones = cantidadItercaciones;

        this.vectorDeEstados.clear();
        this.equipos.clear();
        this.colaComun.clear();
        this.colaTrabajosC.clear();
        this.proximosEventos.clear();

        this.filaActual = null;
        this.filaAnterior = null;
        this.contadorIteraciones = 0;
        this.contadorIteracionesResultado = 0;

        this.reloj = 0;
        this.proximoEvento = null;
        this.contadorEquipos = 0;
        this.vectorDeEstados.clear();

        //Creamos la primer fila, donde solo generamos la primer llegada e inicializamos los contadores y acumuladores
        //en cero. El contador de lugares libres se inicializa en 9.
        double reloj = this.reloj;
        Llegada llegada_primera = new Llegada();
        llegada_primera.generarProximaLlegada(reloj);

        this.proximosEventos.add(new Evento(
                Eventos.Llegada,
                llegada_primera.getHoraProximaLlegada(),
                null
        ));

        ColaVector colaVectorInicio = new ColaVector(
                this.colaComun.size(),
                this.colaTrabajosC.size(),
                0,
                9);

        int contadorEquipos = this.contadorEquipos;

        FinTrabajo finTrabajo = new FinTrabajo();

        Servidor servidorInicio = new Servidor(EstadoServidor.Libre,
                0,
                0);


        // Crea la primer fila, para inciar el trabajo
        this.filaActual = new FilaVector(
                Eventos.Inicio.toString(),
                this.reloj,
                llegada_primera,
                colaVectorInicio,
                contadorEquipos,
                0,
                0,
                finTrabajo,
                servidorInicio,
                clonarEquipos());

        //Se verifica si se debe guardar la fila actual en el vector de estados
        if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones) {
            this.vectorDeEstados.add(this.filaActual);
            this.contadorIteracionesResultado++;
        }

        this.contadorIteraciones++;

        while (this.reloj < this.tiempoSimulacion && this.contadorIteraciones <= 100000) {
            // Siempre al inicio de una iteracion buscamos el proximo evento a ocurrir. Se extrae su hora de
            // ocurrencia y se asigna a la variable reloj, luego se procede a ejecutar el evento correspondiente.
            this.buscarProximoEvento();
            this.filaAnterior = this.filaActual;
            this.reloj = this.proximoEvento.getHoraEvento();

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Llegada)) {
                this.eventoLlegada();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Cambio)) {
                this.eventoCambioTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.Reanudacion)) {
                this.eventoReanudacionTrabajo();
            }

            if (this.proximoEvento.getTipoEvento().equals(Eventos.FinTrabajo)) {
                this.eventoFinTrabajo();
            }

            // Se verifica si se debe guardar la fila actual en el vector de estados en base a los parametros de
            // tiempo incial y cantidad de iteraciones a mostrar
            if (this.reloj >= this.tiempoInicioResultado && this.contadorIteracionesResultado <= this.cantidadItercaciones) {
                this.contadorIteracionesResultado++;
                this.vectorDeEstados.add(this.filaActual);
            }
            this.contadorIteraciones++;
        }

        // Como la ultima fila siempre debe visualizarse, se verifica si no esta ya en el vector de estados a devolver,
        // si no esta se añade al final.
        if (this.vectorDeEstados.getLast() != this.filaActual) {
            this.vectorDeEstados.add(this.filaActual);
        }

        // Se crea el objeto de resultados de la simulacion, donde se calculan los promedios de permanencia y porcentaje
        ResultadosSimulacion resultados = new ResultadosSimulacion();
        resultados.calcularPorcentajeOcupacion(this.reloj, this.filaActual.servidor.getTiempoOcupacionAcum());
        resultados.calcularPromedioPermanencia(this.contadorEquipos, this.filaActual.servidor.getTiempoPermanenciaEquipoAcum());
        resultados.setCantidadFilas(this.vectorDeEstados.size());

        // Si el vector de estados a devolver tiene mas de 200 filas, se devuelven las primeras 200 y la ultima. Caso
        // contrario se devuelven todas las filas.
        if (this.vectorDeEstados.size() > 200) {
            resultados.setFilasPaginadas(this.vectorDeEstados.subList(0, 200));
            resultados.getFilasPaginadas().add(this.vectorDeEstados.getLast());
        } else {
            resultados.setFilasPaginadas(this.vectorDeEstados);
        }
        FilaVector ultimaFila = this.vectorDeEstados.getLast();
        if (!resultados.getFilasPaginadas().contains(ultimaFila)) {
            resultados.getFilasPaginadas().add(ultimaFila);
        }
        return resultados;
    }

    private void eventoFinTrabajo() {

        // Siempre al principio de un evento se copia el estado de las colas
        // y el estado del servidor de la fila anterior.
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        // Siempre al pricipio de un evento,si el servidor estaba ocupado,
        // se acumula el tiempo que estuvo ocupado desde iteracion anterior (Reloj actual - Reloj anterior)
        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        // Se extrae el equipo que finaliza su trabajo
        Equipo equipoFinalizacion = this.proximoEvento.getEquipo();
        FinTrabajo finTrabajo = new FinTrabajo();

        if (colasEstadoActual.getColaTrabajoC() > 0) {
            // Si hay equipos de trabajos C por terminar, se extrae el primero, es decir el que lleva mas tiempo
            // pendiente de terminar. A este equipo se lo remueve de la cola y se le asigna el estado de Atendido.
            Equipo equipoEnColaCAAtender = this.colaTrabajosC.getFirst();
            this.colaTrabajosC.remove(equipoEnColaCAAtender);
            equipoEnColaCAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();

            // Al fin de trabajo se le asigna el tiempo de atencion restante del equipo y
            // se calcula la hora de fin de trabajo del equipo.
            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);

            // Se añade el evento de fin de trabajo del equipo extraido de la cola C que es atendido
            // a la lista de proximos eventos.
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaCAAtender
                    )
            );
        } else if (colasEstadoActual.getColaComun() > 0) {
            // De no haber equipos en la cola de trabajos C pendientes de terminar, se verifica si hay equipos en la
            // cola comun esperando ser atendidos,
            // si los hay se extrae el primero, se remueve de la cola y se le asigna el estado de Atendido.
            colasEstadoActual.restarColaComun();
            Equipo equipoEnColaComun = this.colaComun.getFirst();
            this.colaComun.remove(equipoEnColaComun);
            equipoEnColaComun.setEquipo_estado(EstadoEquipo.Atendido);

            // Se genera el evento de fin de trabajo, calculando la hora de finalizacion de atencion. Se registra esta
            // hora al equipo que se esta atendiendo.
            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComun.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );
            equipoEnColaComun.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            // Se añade el evento de fin de trabajo a la lista de proximos eventos.
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComun
                    )
            );

            // Se verifica si el equipo que se esta atendiendo es de tipo C,
            // si lo es se genera el evento de cambio de trabajo y se añade a la lista de proximos eventos,
            // ademas se registra la hora de cambio de trabajo en el equipo.
            if (equipoEnColaComun.getTipo_trabajo().equals(Trabajo.C)) {
                Double horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                this.reloj + this.tiempoDesdeInicioEquipoC,
                                equipoEnColaComun
                        )
                );
                equipoEnColaComun.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }

        } else {
            // Si no hay equipos en la cola de trabajos C pendientes de terminar ni en la cola comun,
            // se asigna el estado de libre al servidor.
            servidorActual.setEstado(EstadoServidor.Libre);
        }

        // Se actualiza el estado y registra la hora de salida del equipo que finaliza su trabajo.
        equipoFinalizacion.setHora_salida(this.reloj);
        equipoFinalizacion.setEquipo_estado(EstadoEquipo.Finalizado);

        // Se calcula y acumula el tiempo de permanencia del equipo que sale en el sistema.
        double tiempoPermanencia = equipoFinalizacion.getHora_salida() - equipoFinalizacion.getHora_llegada();
        servidorActual.acumTiempoPermanenciaEquipoAcum(tiempoPermanencia);

        // Aca solo se copia la hora de la proxima llegada, ya que el evento no ocurrio ni se modifica.
        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        // Se calcula el porcentaje de ocupacion del servidor en base al tiempo que estuvo ocupado. (Fue calculado y
        // registrado al principio del evento). Tambien al irse un equipo, se calcula el tiempo promedio de permanencia.
        double promedioPermanencia = servidorActual.getTiempoPermanenciaEquipoAcum() / this.contadorEquipos;
        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.FinTrabajo + " E" + equipoFinalizacion.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                promedioPermanencia,
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );
    }

    private void eventoReanudacionTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum);

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        FinTrabajo finTrabajo = new FinTrabajo();

        // Se busca el equipo con trabajo C que deberia reanudar su trabajo.
        Equipo equipoReanudacion = this.proximoEvento.getEquipo();
        equipoReanudacion.setHoraReanudacionTrabajoC(null);

        // Se verifica si el servidor esta ocupado o no.
        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {

            // Si el servidor esta ocupado, se añade el equipo que deberia reanudar su trabajo a la cola C y se
            // le asigna el estado de En cola C. Ademas, como en la cola C estara mas tiempo que su tiempo de atencion
            // calculado en un principio, se anula su hora de fin de atencion estimada y se elimina su evento de fin de
            // trabajo de la lista de proximos eventos.
            // Tambien se copia la hora de fin de trabajo de la fila anterior, que corresponde al equipo con el
            // que el tecnico esta ocupado
            equipoReanudacion.setEquipo_estado(EstadoEquipo.EncolaC);
            equipoReanudacion.setHoraFinAtencionEstimada(null);
            colasEstadoActual.sumarColaTrabajoC();
            this.colaTrabajosC.add(equipoReanudacion);
            this.anularFinTrabajoC(equipoReanudacion.getId_equipo());
            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());
        } else {

            // Si el servidor esta libre, el equipo que deberia reanudar su trabajo es atendido nuevamente,
            // asignandole dicho estado y se asigna la hora de fin de atencion estimada que se habia calculado
            // en un principio. Ademas, se asigna el estado de ocupado al servidor.
            equipoReanudacion.setEquipo_estado(EstadoEquipo.Atendido);
            servidorActual.setEstado(EstadoServidor.Ocupado);
            //finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(equipoReanudacion.getHoraFinAtencionEstimada());
            colasEstadoActual.restarTrabajoCSegundoPlano();
        }

        // Se copia la hora de la proxima llegada, ya que el evento no ocurre ni se modifica.
        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        //double promedioPermanencia = servidorActual.getTiempoPermanenciaEquipoAcum() / this.contadorEquipos;
        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Reanudacion + " E" + equipoReanudacion.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );

    }

    // Esto es para elimiar el evento de fin de trabajo de la lista de proximos eventos cuando cuando un equipo
    // va a la cola C
    private void anularFinTrabajoC(Integer idEquipo) {
        for (Evento evento : this.proximosEventos) {
            if (evento.getTipoEvento().equals(Eventos.FinTrabajo) && evento.getEquipo().getId_equipo() == idEquipo) {
                this.proximosEventos.remove(evento);
                break;
            }
        }
    }


    private void eventoCambioTrabajo() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().tiempoOcupacionAcum,
                this.filaAnterior.getServidor().tiempoPermanenciaEquipoAcum
        );

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        // Se extrae del evento el equipo con trabajo C que sera dejado en segundo plano. Se suma un equipo al contador
        // de equipos y se le asigna el estado de At2doplano.
        Equipo equipoCambioTrabajo = this.proximoEvento.getEquipo();
        equipoCambioTrabajo.setEquipo_estado(EstadoEquipo.At2doplano);
        //this.proximoEvento.getEquipo().setEquipo_estado(EstadoEquipo.At2doplano);
        colasEstadoActual.sumarTrabajoCSegundoPlano();


        // Se calcula la hora de reanudacion del trabajo C, que es la hora de fin de atencion estimada menos el tiempo
        // antes de fin de trabajo en el que debe volverse a atender dicho trabajo. Esa hora se asigna al equipo.
        Double horaReanudacionTrabajoC =
                equipoCambioTrabajo.getHoraFinAtencionEstimada() - this.tiempoAntesFinEquipoC;
        equipoCambioTrabajo.setHoraCambioTrabajoC(null);
        equipoCambioTrabajo.setHoraReanudacionTrabajoC(horaReanudacionTrabajoC);

        // Se genera el evento de reanudacion de trabajo que se añade a la lista de proximos eventos.
        this.proximosEventos.add(
                new Evento(
                        Eventos.Reanudacion,
                        horaReanudacionTrabajoC,
                        equipoCambioTrabajo
                )
        );

        FinTrabajo finTrabajo = new FinTrabajo();

        // Se verifica el estado de las colas
        if (colasEstadoActual.getColaTrabajoC() > 0) {

            // Si hay equipos en la cola de trabajos C, se extrae el primero, se remueve de la cola y se le asigna el
            // estado de Atendido. Se resta un equipo de la cola de trabajos C.
            Equipo equipoEnColaCAAtender = this.colaTrabajosC.getFirst();
            equipoEnColaCAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            colasEstadoActual.restarColaC();

            // Se calcula la hora de fin de trabajo del equipo del trabajo C que se esta atendiendo y se añade el evento de fin de
            // trabajo a la lista de proximos eventos.
            finTrabajo.setTiempoAtencion(this.tiempoAntesFinEquipoC);
            finTrabajo.setHoraFinTrabajo(this.reloj + this.tiempoAntesFinEquipoC);

            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            this.reloj + this.tiempoAntesFinEquipoC,
                            equipoEnColaCAAtender
                    )
            );

        } else if (colasEstadoActual.getColaComun() > 0) {

            // Si no hay equipos en la cola de trabajos C, se verifica si hay equipos en la cola comun, si los hay se
            // extrae el primero, se remueve de la cola y se le asigna el estado de Atendido. Se resta un equipo de la cola
            // comun.
            Equipo equipoEnColaComunAAtender = this.colaComun.getFirst();
            equipoEnColaComunAAtender.setEquipo_estado(EstadoEquipo.Atendido);
            this.colaComun.remove(equipoEnColaComunAAtender);
            colasEstadoActual.restarColaComun();

            // Se calcula la hora de fin de trabajo del equipo de la cola comun que se esta atendiendo y se añade el evento de fin de
            // trabajo a la lista de proximos eventos.
            finTrabajo.calcularHoraFinTrabajo(
                    equipoEnColaComunAAtender.getTipo_trabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme
            );
            this.proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipoEnColaComunAAtender
                    )
            );

            // Se asigna la hora de fin de atencion estimada al equipo que se esta atendiendo.
            equipoEnColaComunAAtender.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            // Se verifica si el trabajo del equipo que se esta atendiendo es de tipo C,
            // si lo es se genera el evento de cambio de trabajo y se añade a la lista de proximos eventos,
            // ademas se registra la hora de cambio de trabajo en el equipo.
            if (equipoEnColaComunAAtender.getTipo_trabajo().equals(Trabajo.C)) {
                double horaCambioTrabajoC = this.reloj + this.tiempoDesdeInicioEquipoC;
                this.proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipoEnColaComunAAtender
                        )
                );
                equipoEnColaComunAAtender.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }
        } else {
            // Si no hay equipos en la cola de trabajos C ni en la cola comun, se asigna el estado de libre al servidor.
            servidorActual.setEstado(EstadoServidor.Libre);
            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());
        }

        // Se copia la hora de la proxima llegada, ya que el evento no ocurre ni se modifica.
        Llegada llegada = new Llegada();
        llegada.setHoraProximaLlegada(this.filaAnterior.llegada.getHoraProximaLlegada());

        //double promedioPermanencia = servidorActual.getTiempoPermanenciaEquipoAcum() / this.contadorEquipos;
        // Se calcula el porcentaje de ocupacion del servidor en base al tiempo que estuvo ocupado. (Fue calculado y
        // registrado al principio del evento).
        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Cambio + " E" + equipoCambioTrabajo.getId_equipo(),
                this.reloj,
                llegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos()
        );
    }

    private void eventoLlegada() {
        ColaVector colasEstadoActual = new ColaVector(
                this.filaAnterior.getColaVector().getColaComun(),
                this.filaAnterior.getColaVector().getColaTrabajoC(),
                this.filaAnterior.getColaVector().getTrabajoCSegundoPlano(),
                this.filaAnterior.getColaVector().getLugaresLibres());

        Servidor servidorActual = new Servidor(
                this.filaAnterior.getServidor().getEstado(),
                this.filaAnterior.getServidor().getTiempoOcupacionAcum(),
                this.filaAnterior.getServidor().getTiempoPermanenciaEquipoAcum());

        if (this.filaAnterior.getServidor().getEstado().equals(EstadoServidor.Ocupado)){
            Double tiempoAAcumular = this.reloj - this.filaAnterior.getReloj();
            servidorActual.acumularIteracionAIteracion(tiempoAAcumular);
        }

        // Se genera la proxima llegada y se añade el evento de llegada a la lista de proximos eventos.
        Llegada proximaLLegada = new Llegada();
        proximaLLegada.generarProximaLlegada(this.reloj);

        this.proximosEventos.add(
                new Evento(
                        Eventos.Llegada,
                        proximaLLegada.getHoraProximaLlegada(),
                        null)
        );

        FinTrabajo finTrabajo = new FinTrabajo();
        Equipo equipo = new Equipo();

        // Se verifica el estado del servidor
        if (servidorActual.getEstado().equals(EstadoServidor.Ocupado)) {

            // Se copia la hora de fin de trabajo de la fila anterior, que corresponde al equipo con el
            // que el tecnico esta ocupado
            finTrabajo.setHoraFinTrabajo(this.filaAnterior.finTrabajo.getHoraFinTrabajo());

            // Si el servidor esta ocupado, se verifica si hay lugares libres en la cola de espera.
            if (colasEstadoActual.getLugaresLibres() > 0) {

                // Se calcula el tipo de trabajo del equipo que acaba de llegar, ademas de que el contador de equipos
                // se incrementa en uno.
                proximaLLegada.calcularTipoTrabajo(tipoTrabajos, probabilidadesTipoTrabajo);
                this.contadorEquipos++;

                // Se le asigna un ID al equipo, se registra su hora de llegada y el tipo de trabajo a realizarle,
                // tambien el equipo es añadido a la lista de equipos existentes en el sistema.
                equipo.setId_equipo(this.contadorEquipos);
                equipo.setHora_llegada(reloj);
                equipo.setTipo_trabajo(proximaLLegada.getTrabajo());
                equipos.add(equipo);

                // Como hay lugarles libres en la cola comun se añade el equipo a la cola comun y se le asigna el
                // estado de En cola. Se resta un lugar libre de la cola de espera.
                colasEstadoActual.sumarColaComun();
                this.colaComun.add(equipo);
                equipo.setEquipo_estado(EstadoEquipo.EnCola);
            }

        } else {
            // Si el servidor esta libre, el equipo recien llegado es atendido de inmediato.
            // Se incrementa el contador de equipos y el servidor se pone en estado ocupado.
            this.contadorEquipos++;
            servidorActual.setEstado(EstadoServidor.Ocupado);

            // Se calcula el tipo de trabajo del equipo que acaba de llegar.
            proximaLLegada.calcularTipoTrabajo(tipoTrabajos, probabilidadesTipoTrabajo);

            // Se genera el fin de trabajo del equipo que acaba de llegar, calculando la hora de fin de atencion.
            finTrabajo.calcularHoraFinTrabajo(
                    proximaLLegada.getTrabajo(),
                    this.tiemposMediaTrabajo,
                    this.reloj,
                    this.limite_inferiorUniforme,
                    this.limite_superiorUniforme);

            // Se le asigna un ID al equipo, se registra su hora de llegada y el tipo de trabajo a realizarle,
            // ademas se le asigna el estado de Atendido. Tambien se registra su hora de fin de atencion estimada.
            equipo.setId_equipo(this.contadorEquipos);
            equipo.setEquipo_estado(EstadoEquipo.Atendido);
            equipo.setTipo_trabajo(proximaLLegada.getTrabajo());
            equipo.setHora_llegada(reloj);
            equipo.setHoraFinAtencionEstimada(finTrabajo.getHoraFinTrabajo());

            // Se añade el evento de fin de trabajo a la lista de proximos eventos.
            proximosEventos.add(
                    new Evento(
                            Eventos.FinTrabajo,
                            finTrabajo.getHoraFinTrabajo(),
                            equipo)
            );

            // Se verifica si el trabajo del equipo que se esta atendiendo es de tipo C,
            // si lo es se genera el evento de cambio de trabajo y se añade a la lista de proximos eventos,
            // ademas se registra la hora de cambio de trabajo en el equipo.
            if (proximaLLegada.getTrabajo().equals(Trabajo.C)) {
                double horaCambioTrabajoC = this.reloj + tiempoDesdeInicioEquipoC;
                proximosEventos.add(
                        new Evento(
                                Eventos.Cambio,
                                horaCambioTrabajoC,
                                equipo)
                );
                equipo.setHoraCambioTrabajoC(horaCambioTrabajoC);
            }
            equipos.add(equipo);
        }

        //double promedioPermanencia = servidorActual.getTiempoPermanenciaEquipoAcum() / this.contadorEquipos;
        // Se calcula el porcentaje de ocupacion del servidor en base al tiempo que estuvo ocupado. (Fue calculado y
        // registrado al principio del evento).
        double porcentajeOcupacion = servidorActual.getTiempoOcupacionAcum() / this.reloj * 100;

        this.filaActual = new FilaVector(
                Eventos.Llegada + " E" + equipo.getId_equipo(),
                this.reloj,
                proximaLLegada,
                colasEstadoActual,
                this.contadorEquipos,
                this.filaAnterior.getPromedioPermanencia(),
                porcentajeOcupacion,
                finTrabajo,
                servidorActual,
                clonarEquipos());
    }


    private ArrayList<Equipo> clonarEquipos() {
        // Este metodo lo que hace es clonar la lista de equipos existentes en el sistema, esto para agregar en cada
        // fila los equipos existentes en el sistema en ese momento. No se añade la lista de equipos existentes en
        // el sistema a la fila actual ya que como los objetos son direcciones de memoria, al modificar un objeto
        // se modificaria tambien en la lista y en todas las filas tendriamos siempre los mismos objetos.
        ArrayList<Equipo> equipos = new ArrayList<>();
        for (Equipo equipo : this.equipos) {
            if (!equipo.isYaTermino()) {
                Equipo equipoClon = new Equipo();
                equipoClon.setId_equipo(equipo.getId_equipo());
                equipoClon.setEquipo_estado(equipo.getEquipo_estado());
                equipoClon.setTipo_trabajo(equipo.getTipo_trabajo());
                equipoClon.setHora_llegada(equipo.getHora_llegada());
                equipoClon.setHoraCambioTrabajoC(equipo.getHoraCambioTrabajoC());
                equipoClon.setHoraReanudacionTrabajoC(equipo.getHoraReanudacionTrabajoC());
                equipoClon.setHoraFinAtencionEstimada(equipo.getHoraFinAtencionEstimada());
                equipoClon.setHora_salida(equipo.getHora_salida());
                equipos.add(equipoClon);
            }
            if (equipo.getEquipo_estado() == EstadoEquipo.Finalizado) {
                equipo.setYaTermino(true);
            }
        }
        return equipos;
    }
}

