package br.com.misterw;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Principal extends JFrame {

	private static final String TITULO_RODAPE = "Desenvolvido por Mister W Informática - Licenciado para Elimart";

	private static final String BUSCA_ESQUEMAS = "Busca Esquemas";

	private static final int TAMANHO_FONTE_PADRAO = 12;

	private static final String FONTE_PADRAO = "Arial";

	private static final String COLUNA_2 = "Ação";

	private static final String COLUNA_1 = "Arquivo";

	private static final String RESULTADO_DA_PESQUISA = "Resultado da Pesquisa ";

	private static final String APP_CONFIG = "app.config";

	private static final long serialVersionUID = 8147516787602099704L;

	private JFrame telaPrincipal;
	private JTextField txtPasta;
	private JTextField txtNome;
	private JTable tabela;
	private List<Arquivo> listaResultado = new ArrayList<Arquivo>();
	private AutoSuggestor autoSuggestor = null;
	private String termoPesquisa = "";
	private JLabel lblResultadoDaPesquisa = new JLabel();
	private DefaultTableModel modelo = new DefaultTableModel();
	private JLabel lblTotal;

	public Principal() {
		super("Busca Esquemas - Elimart");
		this.montaJanela();
	}

	private void montaJanela() {
		montaTela();
		montaTituloPrincipal();
		montaCampoPasta();
		montaCampoNome();
		montaTituloTabela();
		montaTabela();
		carregarAutoComplete();
	}

	private void montaTabela() {
		modelo.addColumn(COLUNA_1);
		modelo.addColumn(COLUNA_2);
		tabela = new JTable(modelo);
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		JScrollPane scroll = new JScrollPane(tabela);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(21, 150, (telaPrincipal.getWidth() - 50), 350);
		tabela.getColumnModel().getColumn(0).setPreferredWidth(331);
		tabela.getColumnModel().getColumn(1).setPreferredWidth(31);
		telaPrincipal.getContentPane().add(scroll);
	}

	private void montaTituloTabela() {
		lblResultadoDaPesquisa.setFont(new Font(FONTE_PADRAO, Font.BOLD, TAMANHO_FONTE_PADRAO));
		lblResultadoDaPesquisa.setBounds(16, 125, (telaPrincipal.getWidth() - 50), 14);
		telaPrincipal.getContentPane().add(lblResultadoDaPesquisa);
	}

	private void montaCampoNome() {
		JLabel lblNome = new JLabel("Nome:");
		lblNome.setFont(new Font(FONTE_PADRAO, Font.BOLD, 12));
		lblNome.setBounds(20, 84, 42, 14);
		telaPrincipal.getContentPane().add(lblNome);

		txtNome = new JTextField();
		txtNome.setBounds(61, 82, 620, 20);
		txtNome.setToolTipText("Informe o nome do esquema");
		telaPrincipal.getContentPane().add(txtNome);
		txtNome.setColumns(10);

		JButton btnNome = new JButton("Buscar");
		btnNome.setBounds(698, 81, 68, 23);
		btnNome.setFont(new Font(FONTE_PADRAO, Font.BOLD, 12));
		btnNome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTermoPesquisa(autoSuggestor.getTextField().getText());
				autoSuggestor.getAutoSuggestionPopUpWindow().setVisible(Boolean.FALSE);
				lblResultadoDaPesquisa.setText(RESULTADO_DA_PESQUISA + " - Busca por: " + getTermoPesquisa());
				listaResultado = obterArquivosPorNome(getTermoPesquisa(), Boolean.TRUE);
				modelo.setNumRows(0);
				for (Arquivo arquivo : listaResultado) {
					new ButtonColumn(tabela, 1);
					modelo.addRow(new Object[] { arquivo.getNome(), arquivo});
				}
				lblTotal.setText("Total: " + listaResultado.size());
			}
		});

		
		telaPrincipal.getContentPane().add(btnNome);
	}

	private void carregarAutoComplete() {
		autoSuggestor = new AutoSuggestor(txtNome, telaPrincipal, null, Color.WHITE.brighter(), Color.BLUE, Color.RED, 1f) {
			@Override
			boolean wordTyped(String typedWord) {
				ArrayList<String> words = new ArrayList<>();
				for (Arquivo arquivo : obterArquivosPorNome("", Boolean.FALSE)) {
					words.add(arquivo.getNome());
				}
				setDictionary(words);
				return super.wordTyped(typedWord);
			}
		};

	}

	private List<Arquivo> obterArquivosPorNome(String termo, Boolean exibeExtensao) {
		return new Service().obterArquivosPorNome(obterPasta(), termo, exibeExtensao);
	}

	private void montaCampoPasta() {
		JLabel lblPasta = new JLabel("Pasta:");
		lblPasta.setFont(new Font(FONTE_PADRAO, Font.BOLD, 12));
		lblPasta.setBounds(20, 38, 42, 21);
		telaPrincipal.getContentPane().add(lblPasta);

		txtPasta = new JTextField();
		txtPasta.setEnabled(false);
		txtPasta.setBounds(61, 36, 650, 23);
		txtPasta.setToolTipText("Local onde será efetuado a pesquisa");
		telaPrincipal.getContentPane().add(txtPasta);
		txtPasta.setText(obterPasta());
		txtPasta.setColumns(10);

		JButton btnPasta = new JButton();
		btnPasta.setText("...");
		btnPasta.setToolTipText("Selecione o arquivo");
		btnPasta.setFont(new Font(FONTE_PADRAO, Font.BOLD, 12));
		btnPasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fc = new JFileChooser();
				// restringe a amostra a diretorios apenas
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int res = fc.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					File diretorio = fc.getSelectedFile();
					String path = diretorio.getPath();
					JOptionPane.showMessageDialog(null, "Voce escolheu o diretório: " + path);
					txtPasta.setText(path);
					salvarNovaPasta(path);
				}
			}

		});
		btnPasta.setBounds(730, 36, 33, 23);
		telaPrincipal.getContentPane().add(btnPasta);
	}

	private String obterPasta() {
		String linha = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(APP_CONFIG));
			while (br.ready()) {
				linha = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linha;
	}

	private void salvarNovaPasta(String path) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(APP_CONFIG));
			writer.write(path);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void montaTituloPrincipal() {
		JLabel lblNewLabel = new JLabel(BUSCA_ESQUEMAS);
		lblNewLabel.setFont(new Font(FONTE_PADRAO, Font.BOLD, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 11, 800, 14);
		telaPrincipal.getContentPane().add(lblNewLabel);
		
		
		JLabel lblNewLabel2 = new JLabel(TITULO_RODAPE);
		lblNewLabel2.setFont(new Font(FONTE_PADRAO, Font.PLAIN, 10));
		lblNewLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel2.setBounds(0, 545, 800, 14);
		lblNewLabel2.setForeground(Color.BLACK);
		telaPrincipal.getContentPane().add(lblNewLabel2);
		
		lblTotal = new JLabel();
		lblTotal.setFont(new Font(FONTE_PADRAO, Font.BOLD, 14));
		lblTotal.setHorizontalAlignment(SwingConstants.LEFT);
		lblTotal.setBounds(21, 505, 800, 14);
		lblTotal.setForeground(Color.BLACK);
		telaPrincipal.getContentPane().add(lblTotal);
	}

	private void montaTela() {
		telaPrincipal = new JFrame();
		telaPrincipal.setTitle("Elimart - " + BUSCA_ESQUEMAS);
		telaPrincipal.setBounds(200, 100, 800, 600);
		telaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		telaPrincipal.getContentPane().setLayout(null);
	}

	public static void main(String[] args) {
		new Principal().telaPrincipal.setVisible(true);

	}

	public String getTermoPesquisa() {
		if (termoPesquisa != null) {
			return termoPesquisa.trim();
		}
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

}