package usecasegen;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.editor.UseCaseDiagramEditor;
import com.change_vision.jude.api.inf.editor.UseCaseModelEditor;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IExtend;
import com.change_vision.jude.api.inf.model.IInclude;
import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class DiagramEditorUtils {

	/**ユースケース図上に，クライアントユースケースからサプライヤーユースケースに依存関係の点線矢印を描く．
	 * @param dgm ユースケース図
	 * @param clientUC クライアントユースケース
	 * @param supplierUC サプライヤーユースケース
	 * @param ps_cUC クライアントユースケースの図の位置
	 * @param ps_sUC サプライヤーユースケースの図の位置
	 * @param stereotype ステレオタイプ
	 * @throws ClassNotFoundException
	 * @throws InvalidEditingException
	 * @throws InvalidUsingException
	 */
    public void createUseCaseDiagramWithDependency(IDiagram dgm, IUseCase clientUC, IUseCase supplierUC, INodePresentation ps_cUC, INodePresentation ps_sUC, String stereotype) throws ClassNotFoundException, InvalidEditingException, InvalidUsingException {

    	//ユースケース図エディターを構築
        UseCaseDiagramEditor ude = AstahAPI.getAstahAPI().getProjectAccessor().getDiagramEditorFactory().getUseCaseDiagramEditor();
        try {
        	//描画トランザクションを開始
            TransactionManager.beginTransaction();
            
            //基本的なモデルエディターを構築
            BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();
            //依存関係を設定
            IDependency depen = basicModelEditor.createDependency(supplierUC, clientUC , "");
            //ステレオタイプを設定
            depen.addStereotype(stereotype);
            
            //ユースケース図の設定
            ude.setDiagram(dgm);
            
            //クライアントユースケースからサプライヤーユースケースに依存関係の点線矢印を描く．
            ude.createLinkPresentation(depen,ps_cUC , ps_sUC);
            //描画トランザクションを終了
            TransactionManager.endTransaction();
        } catch (InvalidEditingException e) {
            e.printStackTrace();
            TransactionManager.abortTransaction();
        }
        
    }
    
  	/**ユースケース図上に，拡張するユースケースから拡張されるユースケースへextendの点線矢印を描く．
  	 * @param dgm ユースケース図
  	 * @param extension 拡張するユースケース
  	 * @param extendedCase 拡張されるユースケース
  	 * @param ps_extension 拡張するユースケースの図の位置
  	 * @param ps_extendedCase 拡張されるユースケースの図の位置
  	 * @throws ClassNotFoundException
  	 * @throws InvalidEditingException
  	 * @throws InvalidUsingException
  	 */
    public void createUseCaseDiagramWithExtend(IDiagram dgm, IUseCase extension, IUseCase extendedCase, INodePresentation ps_extension, INodePresentation ps_extendedCase) throws ClassNotFoundException, InvalidEditingException, InvalidUsingException {
    	//ユースケース図エディターを構築
        UseCaseDiagramEditor ude = AstahAPI.getAstahAPI().getProjectAccessor().getDiagramEditorFactory().getUseCaseDiagramEditor();
        try {
        	//描画トランザクションを開始
            TransactionManager.beginTransaction();
            //ユースケース図モデルエディターを構築
            UseCaseModelEditor useCaseModelEditor = ModelEditorFactory.getUseCaseModelEditor(); 
            //拡張の依存関係を設定
            IExtend iextend = useCaseModelEditor.createExtend(extendedCase, extension, "");
            
            //ユースケース図の設定
            ude.setDiagram(dgm);
            //拡張するユースケースから拡張されるユースケースへextendの点線矢印を描く．
            ude.createLinkPresentation(iextend,ps_extension , ps_extendedCase);
            //描画トランザクションを終了
            TransactionManager.endTransaction();
        } catch (InvalidEditingException e) {
            e.printStackTrace();
            TransactionManager.abortTransaction();
        }
        
    }
    
    /**ユースケース図上に，包含するユースケースから包含されるユースケースへincludeの点線矢印を描く．
     * @param dgm ユースケース図
     * @param includingCase 包含するユースケース
     * @param addition 包含されるユースケース
     * @param ps_includingCase 包含するユースケースの図の位置
     * @param ps_addition 包含されるユースケースの図の位置
     * @throws ClassNotFoundException
     * @throws InvalidEditingException
     * @throws InvalidUsingException
     */
    public void createUseCaseDiagramWithInclude(IDiagram dgm, IUseCase includingCase, IUseCase addition, INodePresentation ps_includingCase, INodePresentation ps_addition) throws ClassNotFoundException, InvalidEditingException, InvalidUsingException {
    	//ユースケース図エディターを構築
        UseCaseDiagramEditor ude = AstahAPI.getAstahAPI().getProjectAccessor().getDiagramEditorFactory().getUseCaseDiagramEditor();
        try {
        	//描画トランザクションを開始
            TransactionManager.beginTransaction();
            //ユースケース図モデルエディターを構築
            UseCaseModelEditor useCaseModelEditor = ModelEditorFactory.getUseCaseModelEditor(); 
            //包含の依存関係を設定
            IInclude iinclude = useCaseModelEditor.createInclude(includingCase, addition, "");
            
            //ユースケース図の設定
            ude.setDiagram(dgm);
            //拡張するユースケースから拡張されるユースケースへextendの点線矢印を描く．
            ude.createLinkPresentation(iinclude,ps_includingCase , ps_addition);
            //描画トランザクションを終了
            TransactionManager.endTransaction();
        } catch (InvalidEditingException e) {
            e.printStackTrace();
            TransactionManager.abortTransaction();
        }
        
    }
    
    
}
