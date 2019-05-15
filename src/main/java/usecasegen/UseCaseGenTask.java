package usecasegen;

import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.editor.UseCaseDiagramEditor;
import com.change_vision.jude.api.inf.editor.UseCaseModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;
import com.change_vision.jude.api.inf.ui.IWindow;

public class UseCaseGenTask extends SwingWorker<Void, Void> {

	private IWindow window;
	private int class_size = 0;
	private int class_progress_count = 0;
	private String class_progress_name = "";

	UseCaseGenTask(IWindow _window){
		window = _window;
	}

    @Override
    public Void doInBackground() throws UnExpectedException {

        setProgress(0);


	    try {

	    	/*
        	 * 情報の取得方法（ここはどうやって取得するかの説明．実際のプログラムには不要）
        	 */

	    	AstahAPI api = AstahAPI.getAstahAPI();
	        ProjectAccessor projectAccessor = api.getProjectAccessor();
	        IModel model = projectAccessor.getProject();

	        List<IClass> classes = this.getClassesOnly(model);
	        IClassUtils icu = new IClassUtils();

	        class_size = classes.size();
	        int progress;

	        StringBuffer strbf = new StringBuffer();
/*
	         strbf.append("<従属しているクラス>\n");
	        for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> depencls = icu.getDependentIClassName(classes.get(i));
	        	for(String depencl : depencls){
	        		strbf.append(depencl + ",");
	        	}
	        	strbf.append("\n");
	        }

	        strbf.append("<従属されているクラス>\n");

	        for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> dependedcls = icu.getDependedIClassName(classes.get(i));
	        	for(String dependedcl : dependedcls){
	        		strbf.append(dependedcl + ",");
	        	}
	        	strbf.append("\n");
	        }
*/
	        strbf.append("<従属されている全てのクラス>\n");

	        for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<Tuple> dependedallcls = icu.getDependedIClassAllName(classes.get(i), classes);
	        	for(Tuple dependedallcl : dependedallcls){
	        		strbf.append( "<" + dependedallcl.getMaster() + "," + dependedallcl.getDepended() + ">");
	        	}
	        	strbf.append("\n");
	        }
/*
	        strbf.append("<コメント>\n");

        	for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> commentbodies = icu.getCommentsBody(classes.get(i));
	        	for(String commentbdy : commentbodies){
	        		strbf.append(commentbdy + ",");
	        	}
	        	strbf.append("\n");
        	}

        	strbf.append("<参照先>\n");

        	for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> suppliers = icu.getSupplierIClassName(classes.get(i));
	        	for(String supplier : suppliers){
	        		strbf.append(supplier + ",");
	        	}
	        	strbf.append("\n");

        	}

        	strbf.append("<継承しているクラス>\n");

        	for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> superclasses = icu.getSuperClass(classes.get(i));
	        	for(String supercl : superclasses){
	        		strbf.append(supercl + ",");
	        	}
	        	strbf.append("\n");

        	}

        	strbf.append("<継承されているクラス>\n");

        	for(int i=0; i<classes.size(); i++){
	        	strbf.append(this.getFullName(classes.get(i)));
	        	strbf.append("⇒");

	        	List<String> subclasses = icu.getSubClass(classes.get(i));
	        	for(String subcl : subclasses){
	        		strbf.append(subcl + ",");
	        	}
	        	strbf.append("\n");

        	}
*/
        	JOptionPane.showMessageDialog(window.getParent(),strbf.toString());

        	/*
        	 * ユースケース図の自動生成
        	 */

        	//各クラスのコメントに対して,CRUDの設定がされているかどうか，二重でCRUDの設定をしていないかをチェック
        	List<String> errorList = this.isCommentAllowedCRUD(classes);
        	if(errorList.size()==0){

        		//各クラスに対して，存在従属に基づいて，ユースケース図を自動生成
        		for(int i=0; i<classes.size(); i++){

        			class_progress_count = i + 1;
        			class_progress_name = classes.get(i).getName();

        			// クラスなどのモデル要素を作成または編集する場合、まとまり単位としてトランザクション操作が必要
                    TransactionManager.beginTransaction();

                    // クラス関連のモデル要素を作成するエディタを取得
                    BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
                    // ユースケース関連のモデル要素を作成するエディタを取得
                    UseCaseModelEditor useCaseModelEditor = ModelEditorFactory.getUseCaseModelEditor();

                    // パッケージを作成
                    IPackage packageUseCase = basicModelEditor.createPackage(model, "UseCase:"+classes.get(i));

                    //コメントに，"CRUD","CRU","CRD","CR",""の中でどの文字列が書かれているかを取得
                    String crudSetting = this.getCRUDSettingFromComment(classes.get(i));

                    //ログインユースケースの作成
                    IUseCase createUsecaseLogin = useCaseModelEditor.createUseCase(packageUseCase, "ログインする");
                    //ログアウトユースケースの作成
                    IUseCase createUsecaseLogout = useCaseModelEditor.createUseCase(packageUseCase, "ログアウトする");
                    //登録ユースケースの作成
                    IUseCase createUsecaseCreate = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を登録");
                    //参照ユースケースの作成
                    IUseCase createUsecaseRead = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を参照");
                    //更新ユースケースの変数
                    IUseCase createUsecaseUpdate = null;
                    //削除ユースケースの変数
                    IUseCase createUsecaseDelete = null;

                    //ユースケース図描画のためのユーティリティ
                    DiagramEditorUtils deu = new DiagramEditorUtils();
                    // ユースケース図の図要素を作成するエディタを取得
                    UseCaseDiagramEditor ude = projectAccessor.getDiagramEditorFactory().getUseCaseDiagramEditor();
                    // ユースケース図の図要素を作成する
                    IDiagram iUseCaseDiagram = ude.createUseCaseDiagram(packageUseCase, classes.get(i).getName());

                    //ログインユースケースの図の位置
                    INodePresentation ps_login = ude.createNodePresentation(createUsecaseLogin, new Point2D.Double(10.0d, 210.0d));
                    //ログアウトユースケースの図の位置
                    INodePresentation ps_logout = ude.createNodePresentation(createUsecaseLogout, new Point2D.Double(10.0d, 350.0d));
                    //登録ユースケースの図の位置
                    INodePresentation ps_create = ude.createNodePresentation(createUsecaseCreate, new Point2D.Double(200.0d, 210.0d));
                    //参照ユースケースの図の位置
                    INodePresentation ps_read = ude.createNodePresentation(createUsecaseRead, new Point2D.Double(200.0d, 400.0d));

                    // トランザクションの終了
                    TransactionManager.endTransaction();

                    //ユースケース図上に，ログインユースケースから登録ユースケースに<<precedes>>の点線矢印を描く．
                    deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseLogin, createUsecaseCreate, ps_login, ps_create, "precedes");
                    //ユースケース図上に，ログインユースケースから参照ユースケースに<<precedes>>の点線矢印を描く．
                    deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseLogin, createUsecaseRead, ps_login, ps_read, "precedes");

                    //存在従属しているクラスリストを取得
                    List<String> dependentClasses = icu.getDependentIClassName(classes.get(i));
                    //存在従属しているクラスのユースケースリスト
                    List<IUseCase> upperClasses = new ArrayList<IUseCase>();
                    //存在従属しているクラスを参照するユースケースの図の位置リスト
                    List<INodePresentation> ps_upper_reads = new ArrayList<INodePresentation>();

                    //従属されているクラスのリスト取得
                    List<String> dependedClasses = icu.getDependedIClassName(classes.get(i));
                    //従属されているクラスのユースケースリスト
                    List<IUseCase> underClasses = new ArrayList<IUseCase>();
                    //従属されているクラスを参照するユースケースの図の位置リスト
                    List<INodePresentation> ps_under_reads = new ArrayList<INodePresentation>();

                    //サブクラスのリスト取得
                    List<String> subClasses = icu.getSubClass(classes.get(i));
                    //サブクラスのユースケースリスト
                    List<IUseCase> subunderClasses = new ArrayList<IUseCase>();
                    //サブクラスを参照するユースケースの図の位置リスト
                    List<INodePresentation> ps_sub_reads = new ArrayList<INodePresentation>();

                    //参照先のクラスのリスト取得
                    List<String> refClasses = icu.getSupplierIClassName(classes.get(i));
                    //参照先のクラスのユースケースリスト
                    List<IUseCase> upperrefClasses = new ArrayList<IUseCase>();
                    //参照先のクラスを参照するユースケースの図の位置リスト
                    List<INodePresentation> ps_upperref_reads = new ArrayList<INodePresentation>();


                    //ユースケースの位置を調整するパラメータ
                    double para3_distance = 0;
                    double para4_distance = 0;
                    //サブクラス毎に，登録・参照ユースケースに対して，サブクラスの登録・参照するユースケースを生成
                    for(String subClass : subClasses){
                    	//トランザクション開始
                    	TransactionManager.beginTransaction();
                    	//サブクラスを参照するユースケースを生成する
                    	IUseCase createUsecaseSubCreate = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を登録");
                    	//サブクラスのユースケースリストに追加
                    	subunderClasses.add(createUsecaseSubCreate);
                    	//サブクラスを参照するユースケースの図の位置を作成
                    	INodePresentation ps_super_create = ude.createNodePresentation(createUsecaseSubCreate, new Point2D.Double(40.0d + para3_distance*150, 100.0d));
                    	//サブクラスを参照するユースケースの図の位置リストに追加
                    	ps_sub_reads.add(ps_super_create);
                    	//トランザクション終了
                    	TransactionManager.endTransaction();

                    	//ユースケース図上に，サブクラスの登録ユースケースから，登録するユースケースへ，<<extend>>の点線矢印を引く
                    	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubCreate, createUsecaseCreate, ps_super_create, ps_create, "extend");
                    	para3_distance ++;

                    	//トランザクション開始
                    	TransactionManager.beginTransaction();
                    	//サブクラスを参照するユースケースを生成する
                    	IUseCase createUsecaseSubRead = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を参照");
                    	//サブクラスのユースケースリストに追加
                    	subunderClasses.add(createUsecaseSubRead);
                    	//サブクラスを参照するユースケースの図の位置を作成
                    	INodePresentation ps_super_read = ude.createNodePresentation(createUsecaseSubRead, new Point2D.Double(30.0d, 450.0d + para4_distance*100));
                    	//サブクラスを参照するユースケースの図の位置リストに追加
                    	ps_sub_reads.add(ps_super_read);
                    	//トランザクション終了
                    	TransactionManager.endTransaction();

                    	//ユースケース図上に，サブクラスの参照ユースケースから，参照するユースケースへ，<<extend>>の点線矢印を引く
                    	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubRead, createUsecaseRead, ps_super_read, ps_read, "extend");
                    	para4_distance ++;

                    	}


                    //コメントにCRUDが設定されていた場合
                    if(crudSetting.equals("CRUD")){
                    	//トランザクション開始
                    	TransactionManager.beginTransaction();

                    	//更新ユースケースの作成
                        createUsecaseUpdate = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を更新");
                        //削除ユースケースの作成
                        createUsecaseDelete = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を削除");

                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //トランザクション開始
                        TransactionManager.beginTransaction();
                        //更新ユースケースの図の位置を作成
                        INodePresentation ps_update = ude.createNodePresentation(createUsecaseUpdate, new Point2D.Double(400.0d, 400.0d));
                        //削除ユースケースの図の位置を作成
                        INodePresentation ps_delete = ude.createNodePresentation(createUsecaseDelete, new Point2D.Double(400.0d, 600.0d));
                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //ユースケース図上に，参照ユースケースから更新ユースケースに<<precedes>>の点線矢印を描く．
                        deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRead, createUsecaseUpdate, ps_read, ps_update, "precedes");
                        //ユースケース図上に，参照ユースケースから削除ユースケースに<<precedes>>の点線矢印を描く．
                        deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRead, createUsecaseDelete, ps_read, ps_delete, "precedes");


                        //ユースケースの位置を調整するパラメータ
                        double para_distance = 0;
                        //存在従属しているクラス毎に，登録ユースケースに対して，存在従属しているクラスを参照するユースケースを生成
                        for(String dependentClass : dependentClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//存在従属しているクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseUpperRead = useCaseModelEditor.createUseCase(packageUseCase, dependentClass+"を参照");
                        	//存在従属しているクラスのユースケースリストに追加
                        	upperClasses.add(createUsecaseUpperRead);
                        	//存在従属しているクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_upper_read = ude.createNodePresentation(createUsecaseUpperRead, new Point2D.Double(750.0d, 210.0d + para_distance*100));
                        	//存在従属しているクラスを参照するユースケースの図の位置リストに追加
                        	ps_upper_reads.add(ps_upper_read);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，存在従属しているクラスを参照するユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseCreate, createUsecaseUpperRead, ps_create,  ps_upper_read,"invokes");
                            para_distance ++;
                        	//ユースケース図上に，存在従属しているクラスの参照ユースケースから参照ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseRead, ps_upper_read, ps_read, "extend");
                            //ユースケース図上に，存在従属しているクラスの参照ユースケースから更新ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseUpdate, ps_upper_read,  ps_update, "extend");
                            //ユースケース図上に，存在従属しているクラスの参照ユースケースから削除ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseDelete, ps_upper_read, ps_delete, "extend");

                        	}


                        /*
                        //ユースケースの位置を調整するパラメータ
                        double para1_distance = 0;
                        //従属されているクラスの更新ユースケースの生成
                        for(String dependedClass : dependedClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//下流クラスの更新ユースケースを生成する
                        	IUseCase createUsecaseUnderUpdate = useCaseModelEditor.createUseCase(packageUseCase, dependedClass+"を更新");
                        	//従属されているクラスのユースケースリストに追加
                        	underClasses.add(createUsecaseUnderUpdate);
                        	//従属されているクラスの更新ユースケースの図の位置を作成
                        	INodePresentation ps_under_update = ude.createNodePresentation(createUsecaseUnderUpdate, new Point2D.Double(200.0d, 500.0d + para1_distance*100));
                        	//従属されているクラスの更新ユースケースの図の位置リストに追加
                        	ps_under_reads.add(ps_under_update);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，下流クラスの更新ユースケースから更新ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUnderUpdate, createUsecaseUpdate, ps_under_update,  ps_update, "extend");
                            para1_distance++;
                        }
                        */
                        /*
                        //ユースケースの位置を調整するパラメータ
                        double para2_distance = 0;
                        //従属されているクラスの削除ユースケースの生成
                        for(String dependedClass : dependedClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//下流クラスの削除ユースケースを生成する
                        	IUseCase createUsecaseUnderDelete = useCaseModelEditor.createUseCase(packageUseCase, dependedClass+"を削除");
                        	//従属されているクラスのユースケースリストに追加
                        	underClasses.add(createUsecaseUnderDelete);
                        	//従属されているクラスの削除するユースケースの図の位置を作成
                        	INodePresentation ps_under_delete = ude.createNodePresentation(createUsecaseUnderDelete, new Point2D.Double(775.0d, 600.0d + para2_distance*100));
                        	//従属されているクラスの更新するユースケースの図の位置リストに追加
                        	ps_under_reads.add(ps_under_delete);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，下流クラスの削除ユースケースから削除ユースケースに<<invokes>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseDelete, createUsecaseUnderDelete, ps_delete, ps_under_delete,  "invokes");
                            para2_distance++;
                        }*/

                        //ユースケースの位置を調整するパラメータ
                        double para7_distance = 0;
                        //サブクラス毎に，更新ユースケースに対して，サブクラスの更新ユースケースを生成
                        for(String subClass : subClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//サブクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseSubUpdate = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を更新");
                        	//サブクラスのユースケースリストに追加
                        	subunderClasses.add(createUsecaseSubUpdate);
                        	//サブクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_super_update = ude.createNodePresentation(createUsecaseSubUpdate, new Point2D.Double(480.0d, 700.0d+para7_distance*100));
                        	//サブクラスを参照するユースケースの図の位置リストに追加
                        	ps_sub_reads.add(ps_super_update);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，サブクラスの登録ユースケースから，登録するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubUpdate, createUsecaseUpdate, ps_super_update, ps_update, "extend");
                        	para7_distance ++;
                        	}

                        //ユースケースの位置を調整するパラメータ
                        double para8_distance = 0;
                        //サブクラス毎に，削除ユースケースに対して，サブクラスの削除ユースケースを生成
                        for(String subClass : subClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//サブクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseSubDelete = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を削除");
                        	//サブクラスのユースケースリストに追加
                        	subunderClasses.add(createUsecaseSubDelete);
                        	//サブクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_super_delete = ude.createNodePresentation(createUsecaseSubDelete, new Point2D.Double(625.0d, 700.0d+para8_distance*100));
                        	//サブクラスを参照するユースケースの図の位置リストに追加
                        	ps_sub_reads.add(ps_super_delete);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，サブクラスの登録ユースケースから，登録ユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubDelete, createUsecaseDelete, ps_super_delete, ps_delete, "extend");
                        	para8_distance ++;
                        	}


                        //ユースケースの位置を調整するパラメータ
                        double para6_distance = 0;
                        //参照先のクラス毎に，参照・更新・削除ユースケースに対して，参照先のクラスの参照ユースケースを生成
                        for(String supplierClass : refClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//参照先のクラスの参照ユースケースを生成する
                        	IUseCase createUsecaseRefRead_1 = useCaseModelEditor.createUseCase(packageUseCase, supplierClass+"を参照");
                        	//参照先のクラスのユースケースリストに追加
                        	upperrefClasses.add(createUsecaseRefRead_1);
                        	//参照先のクラスの参照ユースケースの図の位置を作成
                        	INodePresentation ps_upperref_read_1 = ude.createNodePresentation(createUsecaseRefRead_1, new Point2D.Double(340.0d, 700.0d + para6_distance*100));
                        	//参照先のクラスの参照ユースケースの図の位置リストに追加
                        	ps_upperref_reads.add(ps_upperref_read_1);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，参照しているクラスの参照ユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseCreate, ps_upperref_read_1,ps_create,  "extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，参照ユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseRead , ps_upperref_read_1, ps_read ,"extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，更新ユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseUpdate , ps_upperref_read_1, ps_update ,"extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，削除ユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseDelete , ps_upperref_read_1, ps_delete ,"extend");
                        	para6_distance ++;
                        	}


                        //下流にある全クラスの更新ユースケースの生成
                        List<Tuple> tpu = new ArrayList<Tuple>();
                        tpu = icu.getDependedIClassAllName(classes.get(i), classes);
                        List<IClass> depended_depended_update = new ArrayList<IClass>();
            			depended_depended_update = getTupleDepended(classes.get(i).getName(), tpu);

            			dy=0;
                        recurciveCreateUsecaseUpdate(classes.get(i), depended_depended_update, tpu, createUsecaseUpdate, ps_update, packageUseCase, ude, iUseCaseDiagram,0);

                        useCaseList.clear();
                        useCaseCreateList.clear();

                        //下流にある全クラスの削除ユースケースの生成
                        List<Tuple> tpd = new ArrayList<Tuple>();
                        tpd = icu.getDependedIClassAllName(classes.get(i), classes);
                        List<IClass> depended_depended_delete = new ArrayList<IClass>();
            			depended_depended_delete = getTupleDepended(classes.get(i).getName(), tpd);

            			uy=0;
                        recurciveCreateUsecaseDelete(classes.get(i), depended_depended_delete, tpd, createUsecaseDelete, ps_delete, packageUseCase, ude, iUseCaseDiagram,0);

                        useCaseList.clear();
                        useCaseCreateList.clear();

                    //コメントにCRUが設定されていた場合
            		}else if(crudSetting.equals("CRU")){
            			//トランザクション開始
            			TransactionManager.beginTransaction();

            			//更新ユースケースの作成
                        createUsecaseUpdate = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を更新");

                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //トランザクション開始
                        TransactionManager.beginTransaction();
                        //更新ユースケースの図の位置を作成
                        INodePresentation ps_update = ude.createNodePresentation(createUsecaseUpdate, new Point2D.Double(400.0d, 400.0d));
                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //ユースケース図上に，参照ユースケースから更新ユースケースに<<precedes>>の点線矢印を描く．
                        deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRead, createUsecaseUpdate, ps_read, ps_update, "precedes");

                        //ユースケースの位置を調整するパラメータ
                        double para_distance = 0;
                        //存在従属しているクラス毎に，登録ユースケースに対して，存在従属しているクラスを参照するユースケースを生成
                        for(String dependentClass : dependentClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//存在従属しているクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseUpperRead = useCaseModelEditor.createUseCase(packageUseCase, dependentClass+"を参照");
                        	//存在従属しているクラスのユースケースリストに追加
                        	upperClasses.add(createUsecaseUpperRead);
                        	//存在従属しているクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_upper_read = ude.createNodePresentation(createUsecaseUpperRead, new Point2D.Double(750.0d, 210.0d + para_distance*100));
                        	//存在従属しているクラスを参照するユースケースの図の位置リストに追加
                        	ps_upper_reads.add(ps_upper_read);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，存在従属しているクラスを参照するユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseCreate, createUsecaseUpperRead, ps_create, ps_upper_read, "invokes");
                        	para_distance ++;
                        	//ユースケース図上に，存在従属しているクラスの参照ユースケースから参照ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseRead, ps_upper_read, ps_read, "extend");
                            //ユースケース図上に，存在従属しているクラスの参照ユースケースから更新ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseUpdate, ps_upper_read,  ps_update, "extend");
                        	}

                        /*
                        //ユースケースの位置を調整するパラメータ
                        double para1_distance = 0;
                        //従属されているクラスの更新ユースケースの生成
                        for(String dependedClass : dependedClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//存在従属しているクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseUnderUpdate = useCaseModelEditor.createUseCase(packageUseCase, dependedClass+"を更新");
                        	//存在従属しているクラスのユースケースリストに追加
                        	underClasses.add(createUsecaseUnderUpdate);
                        	//存在従属しているクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_under_read = ude.createNodePresentation(createUsecaseUnderUpdate, new Point2D.Double(200.0d, 500.0d + para1_distance*100));
                        	//存在従属しているクラスを参照するユースケースの図の位置リストに追加
                        	ps_under_reads.add(ps_under_read);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，下流クラスの更新ユースケースから更新ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUnderUpdate, createUsecaseUpdate, ps_under_read,  ps_update, "extend");
                            para1_distance++;
                        }*/

                        //ユースケースの位置を調整するパラメータ
                        double para7_distance = 0;
                        //サブクラス毎に，更新ユースケースに対して，サブクラスの更新ユースケースを生成
                        for(String subClass : subClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//サブクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseSubUpdate = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を更新");
                        	//サブクラスのユースケースリストに追加
                        	subunderClasses.add(createUsecaseSubUpdate);
                        	//サブクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_super_update = ude.createNodePresentation(createUsecaseSubUpdate, new Point2D.Double(480.0d, 700.0d+para7_distance*100));
                        	//サブクラスを参照するユースケースの図の位置リストに追加
                        	ps_sub_reads.add(ps_super_update);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，サブクラスの登録ユースケースから，登録するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubUpdate, createUsecaseUpdate, ps_super_update, ps_update, "extend");
                        	para7_distance ++;
                        	}


                      //ユースケースの位置を調整するパラメータ
                        double para6_distance = 0;
                        //参照先のクラス毎に，参照・更新・削除ユースケースに対して，参照先のクラスの参照ユースケースを生成
                        for(String supplierClass : refClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//参照先のクラスの参照ユースケースを生成する
                        	IUseCase createUsecaseRefRead_1 = useCaseModelEditor.createUseCase(packageUseCase, supplierClass+"を参照");
                        	//参照先のクラスのユースケースリストに追加
                        	upperrefClasses.add(createUsecaseRefRead_1);
                        	//参照先のクラスの参照ユースケースの図の位置を作成
                        	INodePresentation ps_upperref_read_1 = ude.createNodePresentation(createUsecaseRefRead_1, new Point2D.Double(340.0d, 700.0d + para6_distance*100));
                        	//参照先のクラスの参照ユースケースの図の位置リストに追加
                        	ps_upperref_reads.add(ps_upperref_read_1);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，参照しているクラスの参照ユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseCreate, ps_upperref_read_1,ps_create,  "extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，参照するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseRead , ps_upperref_read_1, ps_read ,"extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，更新するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseUpdate , ps_upperref_read_1, ps_update ,"extend");
                        	para6_distance ++;
                        	}

                        //下流にある全クラスの更新ユースケースの生成
                        List<Tuple> tpu = new ArrayList<Tuple>();
                        tpu = icu.getDependedIClassAllName(classes.get(i), classes);
                        List<IClass> depended_depended_update = new ArrayList<IClass>();
            			depended_depended_update = getTupleDepended(classes.get(i).getName(), tpu);

            			uy=0;
                        recurciveCreateUsecaseUpdate(classes.get(i), depended_depended_update, tpu, createUsecaseUpdate, ps_update, packageUseCase, ude, iUseCaseDiagram, 0);

                        useCaseList.clear();
                        useCaseCreateList.clear();


                    //コメントにCRDが設定されていた場合
            		}else if(crudSetting.equals("CRD")){
            			//トランザクション開始
            			TransactionManager.beginTransaction();

                        createUsecaseDelete = useCaseModelEditor.createUseCase(packageUseCase, classes.get(i).getName()+"を削除");

                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //トランザクション開始
                        TransactionManager.beginTransaction();
                        //削除ユースケースの図の位置を作成
                        INodePresentation ps_delete = ude.createNodePresentation(createUsecaseDelete, new Point2D.Double(400.0d, 600.0d));
                        // トランザクションの終了
                        TransactionManager.endTransaction();

                        //ユースケース図上に，参照ユースケースから削除ユースケースに<<precedes>>の点線矢印を描く．
                        deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRead, createUsecaseDelete, ps_read, ps_delete, "precedes");

                        //ユースケースの位置を調整するパラメータ
                        double para_distance = 0;
                        //存在従属しているクラス毎に，登録ユースケースに対して，存在従属しているクラスを参照するユースケースを生成
                        for(String dependentClass : dependentClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//存在従属しているクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseUpperRead = useCaseModelEditor.createUseCase(packageUseCase, dependentClass+"を参照");
                        	//存在従属しているクラスのユースケースリストに追加
                        	upperClasses.add(createUsecaseUpperRead);
                        	//存在従属しているクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_upper_read = ude.createNodePresentation(createUsecaseUpperRead, new Point2D.Double(750.0d, 210.0d + para_distance*100));
                        	//存在従属しているクラスを参照するユースケースの図の位置リストに追加
                        	ps_upper_reads.add(ps_upper_read);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，存在従属しているクラスを参照するユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseCreate, createUsecaseUpperRead, ps_create, ps_upper_read, "invokes");
                        	para_distance ++;
                        	//ユースケース図上に，存在従属しているクラスの参照ユースケースから参照ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseRead, ps_upper_read, ps_read, "extend");
                            //ユースケース図上に，存在従属しているクラスの参照ユースケースから削除ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseDelete, ps_upper_read, ps_delete, "extend");
                        	}

                        /*
                        //ユースケースの位置を調整するパラメータ
                        double para2_distance = 0;
                        //従属されているクラスの削除ユースケースの生成
                        for(String dependedClass : dependedClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//下流クラスの削除するユースケースを生成する
                        	IUseCase createUsecaseUnderDelete = useCaseModelEditor.createUseCase(packageUseCase, dependedClass+"を削除");
                        	//従属されているクラスのユースケースリストに追加
                        	underClasses.add(createUsecaseUnderDelete);
                        	//従属されているクラスの削除するユースケースの図の位置を作成
                        	INodePresentation ps_under_delete = ude.createNodePresentation(createUsecaseUnderDelete, new Point2D.Double(650.0d, 600.0d + para2_distance*100));
                        	//従属されているクラスの更新するユースケースの図の位置リストに追加
                        	ps_under_reads.add(ps_under_delete);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，下流クラスの削除ユースケースから削除ユースケースに<<invokes>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseDelete, createUsecaseUnderDelete, ps_delete, ps_under_delete,  "invokes");
                            para2_distance++;
                        }*/

                        //ユースケースの位置を調整するパラメータ
                        double para6_distance = 0;
                        //参照先のクラス毎に，参照・更新・削除ユースケースに対して，参照先のクラスの参照ユースケースを生成
                        for(String supplierClass : refClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//参照先のクラスの参照ユースケースを生成する
                        	IUseCase createUsecaseRefRead_1 = useCaseModelEditor.createUseCase(packageUseCase, supplierClass+"を参照");
                        	//参照先のクラスのユースケースリストに追加
                        	upperrefClasses.add(createUsecaseRefRead_1);
                        	//参照先のクラスの参照ユースケースの図の位置を作成
                        	INodePresentation ps_upperref_read_1 = ude.createNodePresentation(createUsecaseRefRead_1, new Point2D.Double(340.0d, 700.0d + para6_distance*100));
                        	//参照先のクラスの参照ユースケースの図の位置リストに追加
                        	ps_upperref_reads.add(ps_upperref_read_1);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，参照しているクラスの参照ユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseCreate, ps_upperref_read_1 ,ps_create, "extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，参照するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseRead , ps_upperref_read_1, ps_read ,"extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseDelete , ps_upperref_read_1, ps_delete ,"extend");
                        	para6_distance ++;
                        	}

                        //ユースケースの位置を調整するパラメータ
                        double para8_distance = 0;
                        //サブクラス毎に，削除ユースケースに対して，サブクラスの削除ユースケースを生成
                        for(String subClass : subClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//サブクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseSubDelete = useCaseModelEditor.createUseCase(packageUseCase, subClass+"を削除");
                        	//サブクラスのユースケースリストに追加
                        	subunderClasses.add(createUsecaseSubDelete);
                        	//サブクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_super_delete = ude.createNodePresentation(createUsecaseSubDelete, new Point2D.Double(625.0d, 700.0d+para8_distance*100));
                        	//サブクラスを参照するユースケースの図の位置リストに追加
                        	ps_sub_reads.add(ps_super_delete);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，サブクラスの登録ユースケースから，登録するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseSubDelete, createUsecaseDelete, ps_super_delete, ps_delete, "extend");
                        	para8_distance ++;
                        	}

                        //下流にある全クラスの削除ユースケースの生成
                        List<Tuple> tpd = new ArrayList<Tuple>();
                        tpd = icu.getDependedIClassAllName(classes.get(i), classes);
                        List<IClass> depended_depended_delete = new ArrayList<IClass>();
            			depended_depended_delete = getTupleDepended(classes.get(i).getName(), tpd);
            			dy=0;
                        recurciveCreateUsecaseDelete(classes.get(i), depended_depended_delete, tpd, createUsecaseDelete, ps_delete, packageUseCase, ude, iUseCaseDiagram,0);

                        useCaseList.clear();
                        useCaseCreateList.clear();


                    //コメントにCRが設定されていた場合
            		}else if(crudSetting.equals("CR")){

            			//ユースケースの位置を調整するパラメータ
                        double para_distance = 0;
                        //存在従属しているクラス毎に，登録ユースケースに対して，存在従属しているクラスを参照するユースケースを生成
                        for(String dependentClass : dependentClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//存在従属しているクラスを参照するユースケースを生成する
                        	IUseCase createUsecaseUpperRead = useCaseModelEditor.createUseCase(packageUseCase, dependentClass+"を参照");
                        	//存在従属しているクラスのユースケースリストに追加
                        	upperClasses.add(createUsecaseUpperRead);
                        	//存在従属しているクラスを参照するユースケースの図の位置を作成
                        	INodePresentation ps_upper_read = ude.createNodePresentation(createUsecaseUpperRead, new Point2D.Double(750.0d, 210.0d + para_distance*100));
                        	//存在従属しているクラスを参照するユースケースの図の位置リストに追加
                        	ps_upper_reads.add(ps_upper_read);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，存在従属しているクラスを参照するユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseCreate, createUsecaseUpperRead, ps_create, ps_upper_read, "invokes");
                        	para_distance ++;
                        	//ユースケース図上に，存在従属しているクラスの参照ユースケースから参照ユースケースに<<extend>>の点線矢印を描く．
                            deu.createUseCaseDiagramWithDependency(iUseCaseDiagram,createUsecaseUpperRead, createUsecaseRead, ps_upper_read, ps_read, "extend");
                        }

            			//ユースケースの位置を調整するパラメータ
                        double para6_distance = 0;
                        //参照先のクラス毎に，参照・更新・削除ユースケースに対して，参照先のクラスの参照ユースケースを生成
                        for(String supplierClass : refClasses){
                        	//トランザクション開始
                        	TransactionManager.beginTransaction();
                        	//参照先のクラスの参照ユースケースを生成する
                        	IUseCase createUsecaseRefRead_1 = useCaseModelEditor.createUseCase(packageUseCase, supplierClass+"を参照");
                        	//参照先のクラスのユースケースリストに追加
                        	upperrefClasses.add(createUsecaseRefRead_1);
                        	//参照先のクラスの参照ユースケースの図の位置を作成
                        	INodePresentation ps_upperref_read_1 = ude.createNodePresentation(createUsecaseRefRead_1, new Point2D.Double(340.0d, 700.0d + para6_distance*100));
                        	//参照先のクラスの参照ユースケースの図の位置リストに追加
                        	ps_upperref_reads.add(ps_upperref_read_1);
                        	//トランザクション終了
                        	TransactionManager.endTransaction();

                        	//ユースケース図上に，登録ユースケースから，参照しているクラスの参照ユースケースへ，<<invokes>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseCreate, ps_upperref_read_1 ,ps_create, "extend");
                        	//ユースケース図上に，参照しているクラスの参照ユースケースから，参照するユースケースへ，<<extend>>の点線矢印を引く
                        	deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUsecaseRefRead_1, createUsecaseRead , ps_upperref_read_1, ps_read ,"extend");
                        	para6_distance ++;
                        	}


                    }

                    progress = 99*i/(int)classes.size();
        			setProgress(progress);

        		}

        	}else{
        		//CRUDの設定がされていない，または二重でCRUDの設定をしているエラーを表示
        		StringBuffer errorClassesName = new StringBuffer();

        		for(int i = 0; i<errorList.size(); i++){

        			errorClassesName.append(errorList.get(i) + "クラス");
        			if(i != errorList.size() - 1){
        				errorClassesName.append(", ");
        			}
        		}
        		JOptionPane.showMessageDialog(window.getParent(),"すべてのクラスに\"CRUD\",\"CRU\",\"CRD\",\"CR\"のコメントを追加してください．\nまたは，いずれかのクラスに2個以上のCRUDの設定が追加されています．\n"+ errorClassesName.toString() + "に設定エラーがあります．");
        	}


	    } catch (ProjectNotFoundException e) {
	        String message = "Project is not opened.Please open the project or create new project.";
			JOptionPane.showMessageDialog(window.getParent(), message, "Warning", JOptionPane.WARNING_MESSAGE);
	    } catch (InvalidEditingException e) {
            // トランザクションの放棄
            TransactionManager.abortTransaction();
            // 不正編集の例外メッセージを取得
            System.err.println(e.getMessage());
            e.printStackTrace();

        } catch (Exception e){
	    	JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.:"+e, "Alert", JOptionPane.ERROR_MESSAGE);
	        throw new UnExpectedException();
	    }

	    setProgress(100);

	    return null;
    }

    @Override
    public void done() {
        Toolkit.getDefaultToolkit().beep();
        //JOptionPane.showMessageDialog(window.getParent(),"Done");
    }


	/**
     * 指定パッケージ配下のクラスのみを取得する。
     *
     * @param iPackage
     *            指定パッケージ
     * @return クラス一覧を格納したリスト
     */
    private List<IClass> getClassesOnly(IPackage iPackage) {
        List<IClass> iClasses = new ArrayList<IClass>();
        INamedElement[] iNamedElements = iPackage.getOwnedElements();
        for (int i = 0; i < iNamedElements.length; i++) {
            INamedElement iNamedElement = iNamedElements[i];
            if (iNamedElement instanceof IClass && !(iNamedElement instanceof IUseCase) && !(iNamedElement instanceof IPackage)) {
                iClasses.add((IClass) iNamedElement);
            }
        }
        return iClasses;
    }

    /**
     * クラス(あるいはユースケース)の情報から，フルネームを取得．
     * @param iClass クラス(あるいはユースケース)の情報
     * @return　フルネーム
     */
    private String getFullName(IClass iClass) {
        StringBuffer sb = new StringBuffer();
        IElement owner = iClass.getOwner();
        while (owner != null && owner instanceof INamedElement && owner.getOwner() != null) {
            sb.insert(0, ((INamedElement) owner).getName() + "::");
            owner = owner.getOwner();
        }
        sb.append(iClass.getName());
        return sb.toString();
    }

    /**
     * Get all packages in project.
     * @param project
     *            Project
     * @return Package list
     */
    private List getAllPackages(IModel project) {
        List packages = new ArrayList();
        packages.add(project);
        return getPackages(project, packages);
    }

    /**
     * How to get packages under Package recursively
     * @param iPackage
     *            Selected package
     * @param iPackages
     *            List of all stored packages
     * @return List of all stored packages
     */
    private List getPackages(IPackage iPackage, List iPackages) {
        INamedElement[] iNamedElements = iPackage.getOwnedElements();
        for (int i = 0; i < iNamedElements.length; i++) {
            INamedElement iNamedElement = iNamedElements[i];
            if (iNamedElement instanceof IPackage) {
                iPackages.add(iNamedElement);
                getPackages((IPackage)iNamedElement, iPackages);
            }
        }
        return iPackages;
    }

    /**
     * 各クラスのコメントに対して,CRUDの設定がされているかどうか，二重でCRUDの設定をしていないかをチェック
     * @param classes　各クラス(あるいは各ユースケース)の情報
     * @return　すべてのクラス(あるいはユースケース)に"CRUD","CRU","CRD","CR"のコメントが追加されている，
     * かつ，いずれか(あるいはユースケース)のクラスに2個以上のCRUDの設定が追加されていないければ""を返す．
     * それ以外はエラーしたクラスの名前を全て返す.
     */
    private List<String> isCommentAllowedCRUD(List<IClass> classes){

    	IClassUtils icu = new IClassUtils();
    	int allowedCount = 0;

    	List<String> errorList = new ArrayList<String>();

    	for(IClass iclass : classes){

    		allowedCount = 0;

        	List<String> commentbodies = icu.getCommentsBody(iclass);
        	for(String commentbdy : commentbodies){
        		if(commentbdy.equals("CRUD")){
        			allowedCount++;
        		}else if(commentbdy.equals("CRU")){
        			allowedCount++;
        		}else if(commentbdy.equals("CRD")){
        			allowedCount++;
        		}else if(commentbdy.equals("CR")){
        			allowedCount++;
        		}
        	}

        	if(allowedCount != 1){
        		errorList.add(iclass.getName());
        	}
    	}

    	return errorList;
    }

    /**
     * クラスやユースケースに付加されているコメントに，"CRUD","CRU","CRD","CR",""の中でどの文字列が書かれているかを取得
     * @param iclass　クラスやユースケースの情報
     * @return　"CRUD","CRU","CRD","CR",""いずれかの文字列1つ
     */
    private String getCRUDSettingFromComment(IClass iclass){

    	IClassUtils icu = new IClassUtils();

    	List<String> commentbodies = icu.getCommentsBody(iclass);
    	for(String commentbdy : commentbodies){
    		if(commentbdy.equals("CRUD")){
    			return "CRUD";
    		}else if(commentbdy.equals("CRU")){
    			return "CRU";
    		}else if(commentbdy.equals("CRD")){
    			return "CRD";
    		}else if(commentbdy.equals("CR")){
    			return "CR";
    		}
    	}

    	return "";
    }


    private List<IClass> getTupleDepended(String master_name, List<Tuple> tp){
    	List<IClass> dependedClasses = new ArrayList<IClass>();

    	for(Tuple searched_tp : tp){
    		if(searched_tp.getMaster().getName().equals(master_name)){
    			dependedClasses.add(searched_tp.getDepended());
    		}
    	}
    	return dependedClasses;
    }

    double dy=0;

    List<UseCaseAndNodePresentation> useCaseList = new ArrayList<UseCaseAndNodePresentation>();
    List<UseCaseAndUseCase> useCaseCreateList = new ArrayList<UseCaseAndUseCase>();
    String stereotypeinvokes = "invokes";
    String stereotypeextend = "extend";

    private void recurciveCreateUsecaseDelete(IClass master, List<IClass> depended_classes, List<Tuple> tp, IUseCase master_uc, INodePresentation ps_master, IPackage packageUseCase, UseCaseDiagramEditor ude, IDiagram iUseCaseDiagram,int x){

    	//create usecases

    	for(IClass depended_class : depended_classes){

    		try{
    			//トランザクション開始
    			TransactionManager.beginTransaction();

    			// ユースケース関連のモデル要素を作成するエディタを取得
    			UseCaseModelEditor useCaseModelEditor = ModelEditorFactory.getUseCaseModelEditor();

    			UseCaseAndNodePresentation usecaseAndNode = getUseCaseList(depended_class.getName()+"を削除");
    			IUseCase createUseCaseDelete;
    			INodePresentation ps_depended_delete;

    			if(usecaseAndNode == null){
    				//存在従属しているクラスを削除ユースケースを生成する
    				createUseCaseDelete = useCaseModelEditor.createUseCase(packageUseCase, depended_class.getName()+"を削除");
    				//存在従属しているクラスを削除ユースケースの図の位置を作成
    				ps_depended_delete = ude.createNodePresentation(createUseCaseDelete, new Point2D.Double(750.0d + x*150 ,  600.0d + dy*50));

        			UseCaseAndNodePresentation usecaseAndNodeCreated = new UseCaseAndNodePresentation(createUseCaseDelete, ps_depended_delete);

    				useCaseList.add(usecaseAndNodeCreated);

    			}else{

    				createUseCaseDelete = usecaseAndNode.getIUC();
    				ps_depended_delete = usecaseAndNode.getNodePresentation();

    			}

    			//トランザクション終了
    			TransactionManager.endTransaction();

    			//ユースケース図描画のためのユーティリティ
    			DiagramEditorUtils deu = new DiagramEditorUtils();

    			UseCaseAndUseCase usecaseAndUsecase = getUseCaseCreateList(master_uc, createUseCaseDelete);
    			if(usecaseAndUsecase == null){
    				//ユースケース図上に，削除ユースケースから，存在従属しているクラスの削除ユースケースへ，<<invokes>>の点線矢印を引く
    				deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, master_uc, createUseCaseDelete, ps_master, ps_depended_delete, "invokes");

    				UseCaseAndUseCase usecaseAndUsecaseCreated = new UseCaseAndUseCase(master_uc, createUseCaseDelete, ps_master, ps_depended_delete, stereotypeinvokes);
    				useCaseCreateList.add(usecaseAndUsecaseCreated);
    			}
    			else{
    				createUseCaseDelete = usecaseAndUsecase.getIUC();
    				ps_depended_delete = usecaseAndUsecase.getNodePresentation();
    			}

    			List<IClass> depended_depended = new ArrayList<IClass>();
    			depended_depended = getTupleDepended(depended_class.getName(), tp);

    			if(depended_depended.size()==0){
    				dy++;
    			}

    			this.recurciveCreateUsecaseDelete(depended_class, depended_depended, tp, createUseCaseDelete, ps_depended_delete, packageUseCase, ude, iUseCaseDiagram,x+1);;

    		} catch (InvalidEditingException e) {

                // トランザクションの放棄
                TransactionManager.abortTransaction();
                // 不正編集の例外メッセージを取得
                System.err.println(e.getMessage());
                e.printStackTrace();


            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidUsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    private UseCaseAndNodePresentation getUseCaseList(String usecase_name){

    	for(UseCaseAndNodePresentation iusecaseAndNode : useCaseList){
    		IUseCase iusecase = iusecaseAndNode.getIUC();
    		if(iusecase.getName().equals(usecase_name)){
    			return iusecaseAndNode;
    		}
    	}
    	return null;
    }

    private UseCaseAndUseCase getUseCaseCreateList(IUseCase masterusecase,IUseCase usecase){

    	for(UseCaseAndUseCase iusecaseAndusecase : useCaseCreateList){
    		IUseCase masteriusecase = iusecaseAndusecase.getMIUC();
    		IUseCase iusecase = iusecaseAndusecase.getIUC();
    		if(masteriusecase == masterusecase && iusecase == usecase){
    			return iusecaseAndusecase;
    		}
    	}
    	return null;
    }


    double uy=0;

    private void recurciveCreateUsecaseUpdate(IClass master, List<IClass> depended_classes, List<Tuple> tp, IUseCase master_uc, INodePresentation ps_master, IPackage packageUseCase, UseCaseDiagramEditor ude, IDiagram iUseCaseDiagram, int x){

    	//create usecases

    	for(IClass depended_class : depended_classes){

    		try{
    			//トランザクション開始
    			TransactionManager.beginTransaction();

    			// ユースケース関連のモデル要素を作成するエディタを取得
    			UseCaseModelEditor useCaseModelEditor = ModelEditorFactory.getUseCaseModelEditor();

    			UseCaseAndNodePresentation usecaseAndNode = getUseCaseList(depended_class.getName()+"を更新");
    			IUseCase createUseCaseUpdate;
    			INodePresentation ps_depended_update;

    			if(usecaseAndNode == null){
    				//存在従属しているクラスを更新ユースケースを生成する
    				createUseCaseUpdate = useCaseModelEditor.createUseCase(packageUseCase, depended_class.getName()+"を更新");
    				//存在従属しているクラスを更新ユースケースの図の位置を作成
    				ps_depended_update = ude.createNodePresentation(createUseCaseUpdate, new Point2D.Double(200.0d - x*150, 600.0d + uy*50));

        			UseCaseAndNodePresentation usecaseAndNodeCreated = new UseCaseAndNodePresentation(createUseCaseUpdate, ps_depended_update);

    				useCaseList.add(usecaseAndNodeCreated);
    			}else{
    				createUseCaseUpdate = usecaseAndNode.getIUC();
    				ps_depended_update = usecaseAndNode.getNodePresentation();
    			}

    			//トランザクション終了
    			TransactionManager.endTransaction();

    			//ユースケース図描画のためのユーティリティ
    			DiagramEditorUtils deu = new DiagramEditorUtils();
    			
    			UseCaseAndUseCase usecaseAndUsecase = getUseCaseCreateList(master_uc, createUseCaseUpdate);
    			if(usecaseAndUsecase == null){
    				//ユースケース図上に，更新ユースケースから，存在従属しているクラスの更新ユースケースへ，<<extend>>の点線矢印を引く
    				deu.createUseCaseDiagramWithDependency(iUseCaseDiagram, createUseCaseUpdate, master_uc, ps_depended_update, ps_master, "extend");

    				UseCaseAndUseCase usecaseAndUsecaseCreated = new UseCaseAndUseCase(master_uc, createUseCaseUpdate, ps_master, ps_depended_update, stereotypeextend);
    				useCaseCreateList.add(usecaseAndUsecaseCreated);
    			}
    			else{
    				createUseCaseUpdate = usecaseAndUsecase.getIUC();
    				ps_depended_update = usecaseAndUsecase.getNodePresentation();
    			}

    			List<IClass> depended_depended = new ArrayList<IClass>();
    			depended_depended = getTupleDepended(depended_class.getName(), tp);

    			if(depended_depended.size()==0){
    				uy++;
    			}

    			this.recurciveCreateUsecaseUpdate(depended_class, depended_depended, tp, createUseCaseUpdate, ps_depended_update, packageUseCase, ude, iUseCaseDiagram, x+1);;

    		} catch (InvalidEditingException e) {

                // トランザクションの放棄
                TransactionManager.abortTransaction();
                // 不正編集の例外メッセージを取得
                System.err.println(e.getMessage());
                e.printStackTrace();


            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidUsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public int getClassSize(){
    	return class_size;
    }

    public int getClassProgressCount(){
    	return class_progress_count;
    }

    public String getClassProgressName(){
    	return class_progress_name;
    }
}
