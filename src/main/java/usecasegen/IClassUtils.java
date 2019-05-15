package usecasegen;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IComment;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IGeneralization;

public class IClassUtils {
	
	/**
	 * クラスやユースケースの情報から，存在従属しているクラスやユースケースの名前を取得
	 * @param iclass　クラスやユースケースの情報
	 * @return　存在従属しているクラスやユースケースの名前リスト
	 */
	public List<String> getDependentIClassName(IClass iclass){
    	
    	IAttribute[] attributes = iclass.getAttributes();
    	List<String> dependClasses = new ArrayList<String>();
    	
    	for(int k=0; k<attributes.length; k++){
    		IAttribute attr = attributes[k]; 		
    		if(attr.getAssociation()!=null){
    		
    			if(attr.getNavigability().equals("Navigable")){
    				if(attr.getName().equals("")){
    					dependClasses.add(attr.getTypeExpression());	
    				}else{
    					dependClasses.add(attr.getTypeExpression()+":"+attr.getName());	
    				}
    					
    			}
    		}
    	}
    	
    	return dependClasses;
    }
    
	/**
	 * クラスやユースケースの情報から，存在従属されているクラスやユースケースの名前を取得
	 * @param iclass　クラスやユースケースの情報
	 * @return　存在従属されているクラスやユースケースの名前リスト
	 */
	//下流クラス1層目の取得

	public List<String> getDependedIClassName(IClass iclass){
    	
    	IAttribute[] attributes = iclass.getAttributes();
    	List<String> dependedClasses = new ArrayList<String>();

    	for(int k=0; k<attributes.length; k++){
    		IAttribute attr = attributes[k]; 		
    		if(attr.getAssociation()!=null){
    		
    			if(attr.getNavigability().equals("Unspecified")){
    				
    				int no_name = 0;
    				for(String dependedClassName : dependedClasses){
    					if(attr.getTypeExpression().equals(dependedClassName)){
    						no_name = 1;
    					}
    				}
    				
    				if(no_name == 0){
    					dependedClasses.add(attr.getTypeExpression());
    				}
    				
    			}
    		}
    	}
    	
    	return dependedClasses;
    }

    //全ての下流クラスの取得（取得順はpre-order）
    public List<Tuple> getDependedIClassAllName(IClass iclass, List<IClass> iclasses){

    	//List<String> dependedClasses = new ArrayList<String>();
    	List<Tuple> tupleList = new ArrayList<Tuple>();

    	getDependedIClass(iclass, iclasses, tupleList);

    	return tupleList;
    }

    public List<Tuple> getDependedIClass(IClass iclass, List<IClass> iclasses, List<Tuple> tupleList){

    	IAttribute[] attributes = iclass.getAttributes();

    	for(int k=0; k<attributes.length; k++){
    		IAttribute attr = attributes[k];

    		if(attr.getAssociation()!=null){

    			if(attr.getNavigability().equals("Unspecified")){
    				//dependedClasses.add(attr.getTypeExpression());

    				IClass searched_iclass = searchIClassName(attr.getTypeExpression(), iclasses);

    	    		Tuple tpattr = new Tuple(iclass, searched_iclass);
    	    		
    	    		int no_name = 0;
    				for(Tuple tuple : tupleList){
    					if(iclass.getName().equals(tuple.getMaster().getName())&&searched_iclass.getName().equals(tuple.getDepended().getName())){
    						no_name = 1;
    					}
    				}
    				
    				if(no_name == 0){
    					tupleList.add(tpattr);
    				}
    				
    				getDependedIClass(searched_iclass, iclasses, tupleList);

    			}
    		}
    	}
    	return tupleList;
    }

    public IClass searchIClassName(String iclass_name, List<IClass> iclasses){

    	for(IClass iclass : iclasses){
    		if(iclass.getName().equals(iclass_name)){
    			return iclass;
    		}
    	}
    	return null;
    }
    
    /**
     * クライアントのクラスやユースケースの情報から，サプライヤーのクラスやユースケースの名前を取得
     * @param iclass クライアントのクラスやユースケース
     * @return　サプライヤーのクラスやユースケースの名前リスト
     */
    public List<String> getSupplierIClassName(IClass iclass){
    	
    	IDependency[] dependency = iclass.getClientDependencies();
    	List<String> supplierClasses = new ArrayList<String>();
    	
    	for(int k=0; k<dependency.length; k++){
    		IDependency depen = dependency[k];
    		supplierClasses.add(depen.getSupplier().getName());
    	}
    	
    	return supplierClasses; 	
    }
    
    /**
     * クラスやユースケースの情報から，付加されているコメントを取得
     * @param iclass　クラスやユースケースの情報
     * @return　付加されているコメントリスト
     */
    public List<String> getCommentsBody(IClass iclass){
    	
    	IComment[] comments = iclass.getComments();
    	List<String> commentBodies = new ArrayList<String>();
    	
    	for(int k=0; k<comments.length; k++){
    		IComment comment = comments[k];
    		commentBodies.add(comment.getBody());
    	}
    	
    	return commentBodies;
    }
    
    /**
     * クラスやユースケースの情報から，継承しているクラスやユースケースの名前を取得
     * @param iClass　クラスやユースケースの情報
     * @return　継承しているクラスやユースケースの名前リスト
     */
    public List<String> getSuperClass(IClass iClass) {
    	
        IGeneralization[] generalizations = iClass.getGeneralizations();
        List<String> superClasses = new ArrayList<String>();
        
        for (int i = 0; i < generalizations.length; i++) {
        	superClasses.add(generalizations[i].getSuperType().getName());
        }
        return superClasses;
    }
    
    /**
     * クラスやユースケースの情報から，継承しているクラスやユースケースの名前を取得
     * @param iClass　クラスやユースケースの情報
     * @return　サブクラスやユースケースの名前リスト
     */
   
    public List<String> getSubClass(IClass iClass) {
    	
        IGeneralization[] subgeneralizations = iClass.getSpecializations();
        List<String> subClasses = new ArrayList<String>();
        
        for (int i = 0; i < subgeneralizations.length; i++) {
        	subClasses.add(subgeneralizations[i].getSubType().getName());
        }
        return subClasses;
    }
    
}
