package usecasegen;

import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class UseCaseAndUseCase {
	private IUseCase masteriusecase;
	private INodePresentation masternodepresentation;
	private IUseCase iusecase;
	private INodePresentation nodepresentation;
	private String stereotype;

	public UseCaseAndUseCase(IUseCase iucm ,IUseCase iuc ,INodePresentation inpm ,INodePresentation inp ,String ste){
		masteriusecase = iucm;
		masternodepresentation = inpm;
		iusecase = iuc;
		nodepresentation = inp;
		stereotype = ste;
	}

	public IUseCase getMIUC(){
		return masteriusecase;
	}
	
	public IUseCase getIUC(){
		return iusecase;
	}

	public INodePresentation getNodePresentation(){
		return nodepresentation;
	}
	
	public INodePresentation geMNodePresentation(){
		return masternodepresentation;
	}
	
	public String getStereotype(){
		return stereotype;
	}
	
	public void setIUC(IUseCase iuc){
		iusecase = iuc;
		return;
	}

	public void setNodePresentation(INodePresentation inp){
		nodepresentation = inp;
		return;
	}
	
	public void setMIUC(IUseCase iucm){
		masteriusecase = iucm;
		return;
	}

	public void setMNodePresentation(INodePresentation inpm){
		masternodepresentation = inpm;
		return;
	}
	
	public void setStereotype(String ste){
		stereotype = ste;
		return;
	}
	
}
