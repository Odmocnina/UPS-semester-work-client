package upsSP.Server;

import upsSP.GUI.GameEvaluationScreen;
import upsSP.GUI.Informator;
import upsSP.Nastroje.Constants;
import upsSP.Nastroje.GameState;
import upsSP.Nastroje.States;
import upsSP.Nastroje.Constants;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/************************************************************
 * jedninacek zajistujici komunikaci se serverem, take kontroluje jestli je pripojeni OK
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Connection {
    /** port, na kterem server nasloucha **/
    private int port;
    /** adresa serveru **/
    String adress;
    /** instance singletonu connection **/
    private static Connection instance;
    /** socket pro komunikaci **/
    private Socket socket;
    /** vystupni stream **/
    private PrintWriter out;
    /** vstupni stream **/
    private BufferedReader in;
    /** indikator, zda posloucham **/
    private boolean isLisening = false; //posloucham
    /** indikator, zda posilam **/
    private boolean isSending = false;  //posilam
    /** vlakno pro poslouchani **/
    private Thread lisenThread, pingThread;
    /** id klienta **/
    public int clientId;
    /** listener pro zpravy v queue **/
    private IListenerInQueue lisenerInQueue;
    /** listener pro zpravy ve hre **/
    private IListenerInGame listenerInGame;
    /** listener pro zpravy po tahu **/
    private IListenerAfterTurn listenerAfterTurn;
    /** listener pro zpravy v hodnoceni **/
    private IListenerInJudgement listenerInJudgement;
    /** listener pro zpravy po loginu **/
    private IListenerAfterLogin listenerAfterLogin;
    /** cas posledniho ping **/
    private long time;
    /** indikator, zda je spojeni navazano **/
    boolean connected = false;
    /** pocet pingu **/
    int numberOfPings = 0;
    /** pocet pongu **/
    int numberOfPongs = 0;
    /** zamek pro pristup k promennym **/
    Lock lock = new ReentrantLock();
    /** indikator, zda hra bezi **/
    boolean gameStopped = false;
    /** pole pro indikaci, zda byla zprava odeslana **/
    public boolean[] repeteMessagesSend = {false, false, false, false, false};

    /****
     * soukromy konstruktor pro singletonovou implementaci.
     *
     */
    private Connection() {
    }

    /****
     * metoda pro ziskani instance Singletonu
     *
     *
     * @return instance singletonu Connection.
     */
    public static Connection getInstance() throws IOException {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    /****
     * metoda pro odeslani zpravy serveru a precteni odpovedi
     *
     *
     * @param message zprava, ktera ma byt odeslana
     * @return vraci odpoved serveru
     */
    public String sendMessage(String message) throws IOException {
        out.println(message); //tedka jsem to zmenil z println na print nebo z posilani s lomeno n a bez, tedka bez \n
        out.flush();
        System.out.println("Posilam: " + message);
        return null;
    }

    /****
     * metoda pro prijeti zpravy od serveru
     *
     *
     * @return vraci prijatou zpravu
     */
    public String acceptMessage() throws IOException {
        return in.readLine();
    }

    /****
     * metoda pro uzavreni spojeni
     *
     */
    public void closeConnection() {
        try {
            stopLisening();
            stopPinging();
            sleep(1000); //sleep na dozpracovavani zprav co jsou mozny ze jsou jeste v prubehu;
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            resetConnectionParametres();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /****
     * metoda pro uzavreni spojeni
     *
     */
    public void closeConnectionForTry() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            resetConnectionParametres();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****
     * metoda pro nastaveni portu
     *
     *
     * @param port cislo portu
     */
    private void setPort(int port) {
        this.port = port;
    }

    /****
     * metoda pro nastaveni adresy
     *
     *
     * @param adress adresa serveru
     */
    private void setAdress(String adress) {
        this.adress = adress;
    }

    /****
     * metoda pro ziskani casu pro socket
     *
     *
     * @return cas pro socket
     */
    public int getTimeForSocket() {
        return Constants.NUMBER_OF_PINGS * Constants.TIME_FOR_ONE_PING * 2;
    }

    /****
     * metoda pro pokus o pripojeni
     *
     */
    public void tryToConnect() throws IOException {
        this.socket = new Socket(adress, port);
        //socket.setSoTimeout(getTimeForSocket());
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        setIsConnected(true);
    }

    /****
     * metoda pro nastaveni konfigurace
     *
     * @param port cislo portu
     * @param adress adresa serveru
     * @param startPinging indikator, zda zacit posilat ping
     */
    public void setConfiguration(int port, String adress, boolean startPinging) throws IOException {
        setPort(port);
        setAdress(adress);
        tryToConnect();
        liseningMessegesFromServer();
        sendingPingToServer();
    }

    /****
     * metoda pro prijimani zprav od serveru, nasledne zpracovani a posilani odpovedi
     *
     */
    private void liseningMessegesFromServer() {
        isLisening = true;
        System.out.println("Zapinam posluchadlo");
        lisenThread = new Thread(() -> {
            while (getIsLisening()) {
                //try {
                String message;
                try {
                    message = acceptMessage();
                    if (message == null) {
                        continue;
                    }
                    message = message.trim();
                    if (!message.startsWith("Mess:")) {  //nevalidni zprv
                        System.out.println("Nevlaidni zprva prijata, odpojuji");
                        GameState.getInstance().setScores(0,0, 0);
                        Informator.getInstance(null).informAboutInvalidMessage();
                        closeConnection();
                        break;
                    }
                    System.out.println("Prijata zprava: " + message);
                    //setTime(System.currentTimeMillis());
                    if (message.startsWith("Mess:reconnect:OK:")) {
                        if (!message.equals("Mess:reconnect:OK:")) {
                            processComplicatedReconnect(message);
                            GameEvaluationScreen.aktualizujLably();
                        }
                        Informator.getInstance(null).repairGame();
                        sendNotSendedMessagess();
                    }
                    processOKMessage(message);
                    if (message.startsWith("Mess:pong:")) {
                        setNumberOfPongs(getNumberOfPongs() + 1);
                    }
                    if (message.startsWith("Mess:opponentConnectionProblems:")) {
                        Informator.getInstance(null).informAboutOpponentsFuckedConnection(1);
                    } else if (message.startsWith("Mess:opponentConnectionGood:")) {
                        Informator.getInstance(null).repairGame();

                    } else if (message.startsWith("Mess:opponentConnectionFall:")) {
                        GameState.getInstance().setScores(0, 0, 0);
                        Informator.getInstance(null).informAboutOpponentsFuckedConnection(-1);
                    }
                    if (message.startsWith("Mess:invalidMessage:")) {
                        System.out.println("Nevlaidni zprva poslana, odpojuji");
                        GameState.getInstance().setScores(0,0, 0);
                        Informator.getInstance(null).informAboutInvalidMessage();
                        closeConnection();
                    }
                    if (message.startsWith("Mess:logout:")) {
                        closeConnection();
                        clientId = -1;
                        Informator.getInstance(null).informToShow();
                        GameState.getInstance().setScores(0,0, 0);
                        GameState.getInstance().setState(States.LOGIN);
                        pingThread = null;
                    }
                    if (listenerAfterLogin != null && message != null) {
                        listenerAfterLogin.onMessage(message);
                    }
                    if (lisenerInQueue != null && message != null) {
                        lisenerInQueue.onMessage(message);
                    }
                    if (listenerAfterTurn != null && message != null) {
                        //System.out.print("Prijata zprava: " + message + "\n");
                        listenerAfterTurn.onMessage(message);
                    }
                } catch (IOException cs) {
                    if (getIsLisening()) {
                        cs.printStackTrace();
                    } else {
                        System.out.println("Poslochani bylo zastaveno");
                    }
                    break;
                }
            }
        });
        lisenThread.start();
    }

    /****
     * metoda pro posilani ping zprav na server
     *
     */
    public void sendingPingToServer() {
        isSending = true;
        if (pingThread == null) {  //at se nezacne dalsi ping thread pri reconnectu
            System.out.println("Posilam ping");
            pingThread = new Thread(() -> {
                while (isSending) {
                    try {
                        System.out.println("Pocet pingu: " + getNumberOfPings() + " Pocet pongu: " + getNumberOfPongs());
                        //long timeNow = System.currentTimeMillis();
                        //if (Math.abs(getTime() - timeNow) > 50000 && !connected) {
                    /*if (Math.abs(getNumberOfPongs() - getNumberOfPings()) > Constants.NUMBER_OF_PINGS && !isConnected()) {
                        GameState.getInstance().setScores(0, 0, 0);
                        closeConnection();
                        Informator.getInstance(null).informAboutTimeout();
                        break;
                    }*/
                        //if (Math.abs(getTime() - timeNow) > 5000 && isConnected() == true) { //pokud jeden ping ne tak spatny
                        if (Math.abs(getNumberOfPongs() - getNumberOfPings()) >= 2 && isConnected() == true) { //pokud jeden ping ne tak spatny
                            System.out.println("Problem s spojenim");
                            setIsConnected(false);
                            Informator.getInstance(null).informAboutFuckedConnection();
                            boolean reconnected = reconnect();
                            if (reconnected) {
                                setIsConnected(true);
                            } else {
                                GameState.getInstance().setScores(0, 0, 0);
                                closeConnection();
                                Informator.getInstance(null).informAboutTimeout();
                                break;
                            }
                        } else if (true) {
                            setNumberOfPings(getNumberOfPings() + 1);
                            sendMessage("Mess:ping:" + clientId + ":");
                            sleep(Constants.TIME_FOR_ONE_PING);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            pingThread.start();
        }
    }

    /****
     * metoda pro znovupripojeni, pokud dojde k problemu se spojenim
     *
     *
     * @return vraci true, pokud se pripojeni podarilo
     */
    boolean reconnect() {
        int attempts = 0;
        boolean navrat = false;
        boolean reconnecting = true;
        while (reconnecting) {
            try {
                System.out.println("Zkousim se znovu pripjit. Pokus (" + (attempts + 1) + ")");
                closeConnection(); // Ujistěte se, že staré spojení je uzavřeno
                //setConfiguration(port, adress); // Znovu nastavte konfiguraci a připojte se
                setConfiguration(port, adress, false);
                System.out.println("Spojení bylo obnoveno.");
                reconnecting = false; // Spojení bylo úspěšné
                navrat = true;
                setNumberOfPings(0);
                setNumberOfPongs(0);
                sendMessage("Mess:reconnect:" + clientId + ":");
            } catch (IOException e) {
                attempts = attempts + 1;
                System.out.println("Nepodařilo se připojit. Pokus " + attempts + ".");
                try {
                    sleep(800); // pockej pred dalsim pokusem at se to uplne neposere
                } catch (InterruptedException ignored) {}
            }

            // Zkontrolujte časový limit (např. 50 sekund)
            if (attempts >= Constants.NUMBER_OF_PINGS) {
                System.out.println("Vypršel časový limit pro reconnect. Spojení nelze obnovit.");
                reconnecting = false; // Ukončení pokusů
                //navrat = true;
            }
        }
        return navrat;
    }

    /****
     * metoda pro posilani zpravy pong
     *
     */
    private void pong() throws IOException {
        Connection.getInstance().sendMessage("Mess:pong:\n");
    }

    /****
     * metoda pro zastaveni poslouchani
     *
     */
    private void stopLisening() {
        setIsLisening(false);
        //sleep();
    }


    /****
     * Nastavi hodnotu isLisening na true.
     *
     */
    public void setLiseningToTrue() {
        setIsLisening(true);
    }
    /****
     * Zastavi proces pingovani nastavenim isSending na false.
     *
     */
    private void  stopPinging() {
        isSending = false;
    }


    /**rozhrani pro poslochace v fronte**/
    public interface IListenerInQueue {
        void onMessage(String message);
    }

    /**rozhrani pro poslochace v hre**/
    public interface IListenerInGame {
        void onMessage(String message);
    }

    /**rozhrani pro poslochace po tahu**/
    public interface IListenerAfterTurn {
        void onMessage(String message);
    }

    /**rozhrani pro poslochace v hodnoceni hry**/
    public interface IListenerInJudgement {
        void onMessage(String message);
    }

    /**rozhrani pro poslochace po loginu**/
    public interface IListenerAfterLogin {
        void onMessage(String message);
    }

//    private void pong() {
//
//    }

    /****
     * Nastavi posluchace pro stav ve fronte
     *
     *
     * @param listenerInQueue posluchac pro zpravy ve fronte
     */
    public void addListnerInQueue(IListenerInQueue listenerInQueue) {
        this.lisenerInQueue = listenerInQueue;
    }

    /****
     * Nastavi posluchace pro stav ve hre
     *
     *
     * @param listenerInGame posluchac pro zpravy ve fronte
     */
    public void addListnerInGame(IListenerInGame listenerInGame) {
        this.listenerInGame = listenerInGame;
    }



    /****
     * Nastavi posluchace pro stav po tahu
     *
     *
     * @param listenerAfterTurn posluchac pro zpravy po tahu
     */
    public void addListnerAfterTurn(IListenerAfterTurn listenerAfterTurn) {
        this.listenerAfterTurn = listenerAfterTurn;
    }

    /****
     * Nastavi posluchace pro stav po loginu
     *
     *
     * @param listenerAfterLogin posluchac pro zpravy po tahu
     */
    public void addListnerAfterLogin(IListenerAfterLogin listenerAfterLogin) {
        this.listenerAfterLogin = listenerAfterLogin;
    }

    /****
     * Nastavi posluchace pro stav po vzhdonoceni hrz
     *
     *
     * @param listenerInJudgement posluchac pro zpravy po tahu
     */
    public void addListnerInJudgement(IListenerInJudgement listenerInJudgement) {
        this.listenerInJudgement = listenerInJudgement;
    }

    /****
     * Vraci hodnotu casove promenne
     *
     *
     * @return dlouhe cislo reprezentujici cas
     */
    public long getTime() {
        lock.lock();
        try {
            return time;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavi hodnotu casove promenne
     *
     *
     * @param time dlouhe cislo reprezentujici novy cas
     */
    public void setTime(long time) {
        lock.lock();
        try {
            this.time = time;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci stav pripojeni
     *
     *
     * @return logicka hodnota reprezentujici stav pripojeni
     */
    public boolean isConnected() {
        lock.lock();
        try {
            return connected;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavi stav pripojeni
     *
     *
     * @param connected logicka hodnota reprezentujici novy stav pripojeni
     */
    public void setIsConnected(boolean connected) {
        lock.lock();
        try {
            this.connected = connected;
        } finally {
            lock.unlock();
        }
    }

    /****
     * ziska pocet poslanzch ping zprav
     *
     *
     * @return pocet poslanzch ping zprav
     */
    public int getNumberOfPings() {
        lock.lock();
        try {
            return numberOfPings;
        } finally {
            lock.unlock();
        }
    }

    /****
     * nastavi pocet poslanzch ping zprav
     *
     *
     * @param numberOfPings na kolik chceme nastavit pocet poslanzch ping zprav
     */
    public void setNumberOfPings(int numberOfPings) {
        lock.lock();
        try {
            this.numberOfPings = numberOfPings;
        } finally {
            lock.unlock();
        }
    }

    /****
     * ziska pocet prijatzch pong zprav
     *
     *
     * @return pocet prijatzch pong zprav
     */
    public int getNumberOfPongs() {
        lock.lock();
        try {
            return numberOfPongs;
        } finally {
            lock.unlock();
        }
    }

    /****
     * nastavi pocet prijatzch pong zprav
     *
     *
     * @param numberOfPongs na kolik chceme nastavit pocet prijatzch pong zprav
     */
    public void setNumberOfPongs(int numberOfPongs) {
        lock.lock();
        try {
            this.numberOfPongs = numberOfPongs;
        } finally {
            lock.unlock();
        }
    }

    /****
     * funkce, ktera zjisti jestli klient posloucha
     *
     *
     * @return boolean hodnota vzjadrici jestli klient poslouch
     **/
    public boolean getIsLisening() {
        lock.lock();
        try {
            return this.isLisening;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavuje priznak pro odeslani zpravy o tahu
     *
     */
    public void turnSend() {
        repeteMessagesSend[0] = true;
    }

    /****
     * Nastavuje priznak pro odeslani zpravy o dalsim kole
     *
     */
    public void nextRoundSend() {
        repeteMessagesSend[0] = true;
    }

    /****
     * Nastavuje, zda server nasloucha prichozim zpravam
     *
     *
     * @param isLisening Priznak, zda ma server naslouchat
     */
    public void setIsLisening(boolean isLisening) {
        lock.lock();
        try {
            this.isLisening = isLisening;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Resetuje parametry pripojeni, vcetne poctu pingu, pongu a stavu pripojeni
     *
     */
    public void resetConnectionParametres() {
        setNumberOfPings(0);
        setNumberOfPongs(0);
        setIsConnected(false);
    }

    /****
     * Nastavuje, zda byla odeslana prihlasovaci zprava
     *
     * @param turnSend Priznak odeslani
     */
    public void setLoginSend(boolean turnSend) {
        lock.lock();
        try {
            repeteMessagesSend[0] = turnSend;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci, zda byla prihlasovaci zprava odesln
     *
     *
     * @return Priznak odeslani prihlasovaci zpravy
     */
    public boolean getLoginSend() {
        lock.lock();
        try {
            return repeteMessagesSend[0];
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavuje, zda byla odeslana zprava o tahu
     *
     *
     * @param turnSend Priznak odeslani
     */
    public void setLogoutSend(boolean turnSend) {
        lock.lock();
        try {
            repeteMessagesSend[1] = turnSend;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci, zda byla zprava o tahu odesln
     *
     *
     * @return Priznak odeslani zpravy o tahu
     */
    public boolean getLogoutSend() {
        lock.lock();
        try {
            return repeteMessagesSend[1];
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavuje, zda byla odeslana zprava o tahu
     *
     *
     * @param turnSend Priznak odeslani
     */
    public void setTurnSend(boolean turnSend) {
        lock.lock();
        try {
            repeteMessagesSend[2] = turnSend;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci, zda byla zprava o tahu odesln
     *
     *
     * @return Priznak odeslani zpravy o tahu
     */
    public boolean getTurnSend() {
        lock.lock();
        try {
            return repeteMessagesSend[2];
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavuje, zda byla odeslana zprava o dalsim kole
     *
     *
     * @param turnSend Priznak odeslani
     */
    public void setNextRoundSend(boolean turnSend) {
        lock.lock();
        try {
            repeteMessagesSend[3] = turnSend;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci, zda byla zprava o dalsim kole odesln
     *
     *
     * @return Priznak odeslani zpravy o dalsim kole
     */
    public boolean getNextRoundSend() {
        lock.lock();
        try {
            return repeteMessagesSend[3];
        } finally {
            lock.unlock();
        }
    }

    /****
     * Nastavuje, zda byla odeslana zprava o hre
     *
     *
     * @param turnSend Priznak odeslani
     */
    public void setGameSend(boolean turnSend) {
        lock.lock();
        try {
            repeteMessagesSend[4] = turnSend;
        } finally {
            lock.unlock();
        }
    }

    /****
     * Vraci, zda byla zprava o hre odesln
     *
     *
     * @return Priznak odeslani zpravy o hre
     */
    public boolean getGameSend() {
        lock.lock();
        try {
            return repeteMessagesSend[4];
        } finally {
            lock.unlock();
        }
    }

    /****
     * Odesila zpravy, ktere nebyly dosud odesln
     *
     *
     * @throws IOException Pokud nastane chyba pri odesilani
     */
    public void sendNotSendedMessagess() throws IOException {
        if (getLoginSend()) {
            sendMessage("Mess:login:" + GameState.getInstance().turnValue + ":");
            setLoginSend(false);
        }
        if (getLogoutSend()) {
            sendMessage("Mess:login:" + GameState.getInstance().turnValue + ":");
            setLogoutSend(false);
        }
        if (getNextRoundSend()) {
            sendMessage("Mess:readyForNextRound:" + clientId + ":");
            setNextRoundSend(false);
        }
        if (getTurnSend()) {
            sendMessage("Mess:turn:" + GameState.getInstance().turnValue + ":");
            setTurnSend(false);
        }
        if (getGameSend()) {
            sendMessage("Mess:game:" + Connection.getInstance().clientId + ":");
            setTurnSend(false);
        }
    }


    /****
     * Zpracovava zpravy typu OK a nastavi odpovidajici priznak pro odesln
     *
     *
     * @param message Zprava, kterou je nutno zpracovat
     */
    public void processOKMessage(String message) {
        if (message.startsWith("Mess:login:")) {
            setLoginSend(false);
        } else if (message.startsWith("Mess:logout:")) {
            setLogoutSend(false);
        } else if (message.startsWith("Mess:turn:OK:")) {
            setTurnSend(false);
        } else if (message.startsWith("Mess:readyForNextRound:OK:")) {
            setNextRoundSend(false);
        } else if (message.startsWith("Mess:game:OK:")) {
            setGameSend(false);
        }
    }

    /****
     * Zpracovava slozite obnoveni spojeni na zaklade prijate zprv
     *
     *
     * @param message Zprava, kterou je nutno zpracovat pro obnoveni spojeni
     */
    public void processComplicatedReconnect(String message) {
        String[] parts = message.split(":");
        try {
            if (parts[3].equals("n") && GameState.getInstance().stateOfGame == States.QUEUE) {
                GameState.getInstance().setState(States.GAME);
            } else if (parts[3].equals("w") && GameState.getInstance().stateOfGame == States.AFTERTURN) {
                GameState.getInstance().addWin();
                GameState.getInstance().setState(States.GAMEJUDGEMENT);
                GameEvaluationScreen.setOpponentTurn(Integer.parseInt(parts[4]));
                GameEvaluationScreen.setRoundResultLabel("Výhra");
            } else if (parts[3].equals("l") && GameState.getInstance().stateOfGame == States.AFTERTURN) {
                GameState.getInstance().addLoss();
                GameState.getInstance().setState(States.GAMEJUDGEMENT);
                GameEvaluationScreen.setOpponentTurn(Integer.parseInt(parts[4]));
                GameEvaluationScreen.setRoundResultLabel("Prohra");
            } else if (parts[3].equals("s") && GameState.getInstance().stateOfGame == States.AFTERTURN) {
                GameState.getInstance().addStaleMate();
                GameState.getInstance().setState(States.GAMEJUDGEMENT);
                GameEvaluationScreen.setOpponentTurn(Integer.parseInt(parts[4]));
                GameEvaluationScreen.setRoundResultLabel("Remíza");
            }
        } catch (NumberFormatException e) {
            System.out.println("Na miste cisla jinaci znaky");
        }
    }



}
