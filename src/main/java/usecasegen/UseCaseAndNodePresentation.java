package usecasegen;

import com.change_vision.jude.api.inf.model.IUseCase;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class UseCaseAndNodePresentation {
	private IUseCase iusecase;
	private INodePresentation nodepresentation;

	public UseCaseAndNodePresentation(IUseCase iuc, INodePresentation inp){
		iusecase = iuc;
		nodepresentation = inp;
	}

	public IUseCase getIUC(){
		return iusecase;
	}

	public INodePresentation getNodePresentation(){
		return nodepresentation;
	}

	public void setIUC(IUseCase iuc){
		iusecase = iuc;
		return;
	}

	public void setNodePresentation(INodePresentation inp){
		nodepresentation = inp;
		return;
	}
}
